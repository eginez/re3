package xyz.eginez.re3;

import java.util.*;

interface Connected<T> {
    void connect(T newT);
    void clearConnection(T c);
}
class State implements Connected<State> {
    int c;
    State next;
    State next2;

    public static final State MATCH = new State(0, null, null);
    private static final int SPLIT = -2;

    State(char c, State next, State next2) {
        this((int) c, next, next2);
    }

    private State(int c, State next, State next2) {
        this.c = c;
        this.next = next;
        this.next2 = next2;
    }

    public void clearConnection(State s) {
        if (s == next) {
            next = null;
        }
        assert next2 == s;
        next2 = null;
    }

    static State Split(State next, State next2) {
        return new State(SPLIT, next, next2);
    }

    public void connect(State s) {
        next = next == null ? s : next;
        next2 = next2 == null ? s : next2;
    }

    boolean isSplit() {
        return c == SPLIT;
    }

    @Override
    public String toString() {
        if (this == MATCH) {
            return "MATCH";
        }

        String cs = isSplit() ? "SPLIT" : String.format("%s", (char) c);
        return "State{" +
                "c=" + cs +
                ", next=" + next +
                ", next2=" + next2 +
                '}';
    }
}

class Fragment<T extends Connected<T>> {
    T start;
    List<T> outStates;

    Fragment(T start, T state) {
        this(start, Collections.singletonList(state));
    }

    Fragment(T start, List<T> outStates) {
        assert outStates != null;
        this.start = start;
        this.outStates = outStates;
    }

    void connect(final T s) {
        outStates.forEach(out -> out.connect(s));
    }
}


public class Matcher {
    private final String regex;
    private final State start;

    public Matcher(String regex) {
        this.regex = regex;
        start = parsePostfixRegex(regex);
    }

    public String getRegex() {
        return regex;
    }

    protected State getStart() {
        return start;
    }

    private static <T> List<T> concatenate(List<T> elements, T more) {
        List<T> all = new ArrayList<>(elements);
        Collections.addAll(all, more);
        return all;
    }

    private static <T> List<T> concatenate(List<T> elements, List<T> more) {
        List<T> all = new ArrayList<>(elements);
        all.addAll(more);
        return all;
    }

    private State parsePostfixRegex(String regex) {
        assert !regex.isEmpty();

        Deque<Fragment<State>> stack = new LinkedList<>();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            Fragment<State> fragment;
            Fragment<State> lastFragment = null;
            State newState;
            switch (c) {
                case '*':
                    lastFragment = stack.pop();
                    newState = State.Split(lastFragment.start, null);
                    fragment = new Fragment(newState, newState);
                    lastFragment.connect(newState);
                    break;
                case '+':
                    lastFragment = stack.pop();
                    newState = State.Split(lastFragment.start, null);
                    lastFragment.connect(newState);
                    fragment = new Fragment(lastFragment.start, newState);
                    break;
                case '?':
                    lastFragment = stack.pop();
                    newState = State.Split(lastFragment.start, null);
                    fragment = new Fragment(newState, concatenate(lastFragment.outStates, newState));
                    break;
                case '|':
                    lastFragment = stack.pop();
                    Fragment<State> beforeLast = stack.pop();
                    newState = State.Split(lastFragment.start, beforeLast.start);
                    beforeLast.outStates = Collections.singletonList(beforeLast.start);
                    beforeLast.start.clearConnection(lastFragment.start);
                    fragment = new Fragment(newState, concatenate(lastFragment.outStates, beforeLast.outStates));
                    break;
                default:
                    newState = new State(c, null, null);
                    Fragment newFragment = new Fragment(newState, newState);
                    if (!stack.isEmpty()) {
                        Fragment bf = stack.peek();
                        bf.connect(newFragment.start);
                    }
                    fragment = newFragment;
                    break;
            }
            stack.push(fragment);
        }

        assert !stack.isEmpty();
        Fragment last = stack.getFirst();
        last.connect(State.MATCH);
        return stack.getLast().start;
    }


    public boolean matchAny(String string) {
        Set<State> currStates = new HashSet<>();
        addState(currStates, start);

        for (int i = 0; i < string.length(); i++) {
            currStates = step(currStates, string.charAt(i));
        }

        return currStates.contains(State.MATCH);
    }

    private static void addState(Set<State> acc, State s) {
        if (s == null) {
            return;
        }

        if (s.isSplit()) {
            addState(acc, s.next);
            addState(acc, s.next2);
            return;
        }
        acc.add(s);
    }

    private static Set<State> step(Set<State> currentStates, char c) {
        HashSet<State> newStates = new HashSet<>();
        for (State currS : currentStates) {
            if (currS == State.MATCH) {
                newStates.add(State.MATCH);
                break;
            }

            if (currS.c == c) {
                addState(newStates, currS.next);
            }
        }
        return newStates;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong arguments.Expected a regex and a string");
            System.exit(-1);
        }

        final Matcher matcher = new Matcher(args[0]);

        String result = matcher.matchAny(args[1]) ? "matched" : "not-matched";
        System.out.println(result);
    }
}


