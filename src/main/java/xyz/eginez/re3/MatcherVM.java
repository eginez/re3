package xyz.eginez.re3;

import java.util.*;

import static xyz.eginez.re3.MatcherVM.Node.*;


public class MatcherVM {
    static class Node implements Connected<Node> {
        private int opCode;
        int c;
        Node next;
        Node next2;

        public static final byte MATCH_OPCODE = 0;
        public static final byte CHAR_OPCODE = 100;
        public static final byte SPLIT_OPCODE = 101;
        public static final byte JUMP_OPCODE = 102;

        private Node(int opCode, int c, Node next, Node next2) {
            this.opCode = opCode;
            this.c = c;
            this.next = next;
            this.next2 = next2;
        }

        public static Node Char(int c) {
            return new Node(CHAR_OPCODE, c, null, null);
        }

        public static Node Split(Node left, Node right) {
            return new Node(SPLIT_OPCODE, 0, left, right);
        }

        public static Node Jump(Node next) {
            return new Node(JUMP_OPCODE, 0, next, next);
        }

        public static Node Match() {
            return new Node(0, 0, null, null);
        }

        public void connect(Node s) {
            next = next == null ? s : next;
            next2 = next2 == null ? s : next2;
        }

        public void clearConnection(Node s) {
            if (s == next) {
                next = null;
            }

            if (s == next2) {
                next2 = null;
            }
        }

        @Override
        public String toString() {
            switch (opCode) {
                case MATCH_OPCODE:
                    return "match";
                case JUMP_OPCODE:
                    return "jump";
                case SPLIT_OPCODE:
                    return "split";
                case CHAR_OPCODE:
                    return "char " + (char) c;
            }
            assert false;
            return null;
        }
    }

    final String regex;
    final Node start;

    public MatcherVM(String regex) {
        this.regex = regex;
        start = parse(regex);
    }

    public String getRegex() {
        return regex;
    }

    private Node parse(String regex) {
        if (regex == null || regex.isEmpty()) {
            throw new RuntimeException("Regex must be not empty");
        }

        Deque<LinkedList<Fragment<Node>>>  allFrames = new LinkedList<>();
        allFrames.push(new LinkedList<>());
        LinkedList<Fragment<Node>> currentFrame;

        for (int i = 0; i < regex.length(); i++) {
            currentFrame = allFrames.peek();
            assert currentFrame != null;
            char c = regex.charAt(i);
            switch (c) {
                case '*':
                    processZeroMore(currentFrame);
                    break;
                case '(':
                    allFrames.push(new LinkedList<>());
                    break;
                case ')':
                    closeParen(allFrames);
                    break;
                default:
                    processLiteral(c, currentFrame);
                    break;
            }
        }
        LinkedList<Fragment<Node>> lastFrame = allFrames.peek();
        lastFrame.getFirst().connect(Node.Match());
        return lastFrame.getLast().start;
    }

    private void closeParen(Deque<LinkedList<Fragment<Node>>>  all) {
        LinkedList<Fragment<Node>> currentFrame = all.pop();
        Node lastStart = currentFrame.getLast().start;
        Fragment<Node> newFragment = new Fragment<>(lastStart, currentFrame.getFirst().outStates);
        assert !all.isEmpty();
        LinkedList<Fragment<Node>> lastFrame = all.peek();
        if(!lastFrame.isEmpty()) {
            lastFrame.peek().connect(newFragment.start);
        }
        lastFrame.push(newFragment);
    }

    public void processLiteral(char c, Deque<Fragment<Node>> stack) {
        Node newInstruction = Node.Char(c);
        Fragment<Node> newFragment = new Fragment<>(newInstruction, newInstruction);
        if (!stack.isEmpty()) {
            Fragment<Node> last = stack.peek();
            last.connect(newFragment.start);
        }
        stack.push(newFragment);
    }

    public void processZeroMore(Deque<Fragment<Node>> stack) {
        Fragment<Node> last = stack.pop();
        Node split = Node.Split(last.start, null);
        Fragment<Node> newFragment = new Fragment<>(split, split);
        if (!stack.isEmpty()) {
            Fragment<Node> bl = stack.peek();
            bl.start.clearConnection(last.start);
            bl.connect(newFragment.start);
        }
        Node jump = Node.Jump(newFragment.start);
        last.connect(jump);
        stack.push(newFragment);
    }

    public static String instructions(List<Instruction> all) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < all.size(); i++) {
            sb.append(String.format("%d %s\n", i, all.get(i).toString()));
        }
        return sb.toString();
    }

    public List<Instruction> toByteCode() {
        Map<Node, Map.Entry<Instruction, Integer>> flattenNodes = toByteCode(start, new LinkedHashMap<>());
        List<Instruction> instructions = new ArrayList<>();
        flattenNodes.forEach((n, v) -> {
            if (n.opCode == JUMP_OPCODE) {
                assert flattenNodes.containsKey(n.next);
                Integer jumpTo = flattenNodes.get(n.next).getValue();
                instructions.add(new JumpInstruction(jumpTo));
            } else if (n.opCode == SPLIT_OPCODE) {
                assert flattenNodes.containsKey(n.next);
                assert flattenNodes.containsKey(n.next2);
                Integer split1 = flattenNodes.get(n.next).getValue();
                Integer split2 = flattenNodes.get(n.next2).getValue();
                instructions.add(new SplitInstruction(split1, split2));
            } else {
                instructions.add(v.getKey());
            }
        });
        return instructions;
    }

    private Map<Node, Map.Entry<Instruction, Integer>> toByteCode(Node current, Map<Node, Map.Entry<Instruction, Integer>> visited) {
        if (current == null || visited.containsKey(current)) {
            return visited;
        }

        int size = visited.size();
        if (current.opCode == CHAR_OPCODE) {
            Instruction n = new CharInstruction(current.c);
            visited.put(current, new AbstractMap.SimpleEntry<>(n, size));
            toByteCode(current.next, visited);
        } else if (current.opCode == MATCH_OPCODE) {
            Instruction n = new MatchInstruction();
            visited.put(current, new AbstractMap.SimpleEntry<>(n, size));
        } else if (current.opCode == JUMP_OPCODE) {
            visited.put(current, new AbstractMap.SimpleEntry<>(null, size));
            toByteCode(current.next, visited);
        } else if (current.opCode == SPLIT_OPCODE) {
            visited.put(current, new AbstractMap.SimpleEntry<>(null, size));
            toByteCode(current.next, visited);
            toByteCode(current.next2, visited);
        }
        return visited;
    }

}
