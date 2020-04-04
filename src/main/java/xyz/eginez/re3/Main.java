package xyz.eginez.re3;

import java.util.*;

class State {
    int c;
    State next;
    State next2;

    public static final State MATCHED = new State(0, null, null);
    private static final int NO_OP = -1;
    private static final int SPLIT = -2;

    State(char c, State next, State next2) {
        this((int) c, next, next2);
    }

    private State(int c, State next, State next2) {
        this.c = c;
        this.next = next;
        this.next2 = next2;
    }

    static State Noop() {
        return new State(NO_OP, null, null);
    }

    static State Split(State next, State next2) {
        return new State(SPLIT, next, next2);
    }

    void connect(State s) {
        next = next == null ? s : next;
        next2 = next2 == null ? s : next2;
    }

    boolean isNoop() {
        return c == NO_OP;
    }

    boolean isSplit() {
        return c == SPLIT;
    }

    @Override
    public String toString() {
        if (this == MATCHED) {
            return "MATCHED";
        }
        if (this.isNoop()) {
            return "NOOP";
        }

        return "State{" +
                "c=" + c +
                ", next=" + next +
                ", next2=" + next2 +
                '}';
    }
}

class Fragment {
    State start;
    List<State> outStates;

    Fragment(State start, State state) {
        this(start, Collections.singletonList(state));
    }

    Fragment(State start, List<State> outStates) {
        assert outStates != null;
        this.start = start;
        this.outStates = outStates;
    }

    void connect(final State s) {
        outStates.forEach(out -> out.connect(s));
    }
}

class Matcher {
    private final String regex;
    private final State start;

    public Matcher(String regex) {
        this.regex = regex;
        start = parseRegex(regex);
    }

    public State getStart() {
        return start;
    }

    private State parseRegex(String regex) {
        Stack<Fragment> stack = new Stack<>();
        State init = State.Noop();
        stack.push(new Fragment(init, init));

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            Fragment fragment;
            Fragment popped = null;
            switch (c) {
                case '*':
                    popped = stack.pop();
                    State s = State.Split(popped.start, null);
                    fragment = new Fragment(s, s);
                    popped.connect(s);
                    break;
                default:
                    popped = stack.pop();
                    State st = new State(c, null, null);
                    popped.connect(st);
                    fragment = new Fragment(popped.start, st);
                    break;
            }
            stack.push(fragment);
        }

        final Fragment whole = stack.pop();
        whole.connect(State.MATCHED);
        return whole.start;
    }

    public boolean match(String string) {
        Set<State> currStates = new HashSet<>();
        addState(currStates, start);

        for (int i = 0; i < string.length(); i++) {
            currStates = step(currStates, string.charAt(i));
        }

        return currStates.contains(State.MATCHED);
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
        currentStates.forEach(s -> {
            State currS = s.isNoop() ? s.next : s;
            if (currS == State.MATCHED) {
                newStates.add(State.MATCHED);
                return;
            }

            if (currS.c == c) {
                addState(newStates, currS.next);
            }
        });
        return newStates;
    }
}

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong arguments.Expected a regex and a string");
            System.exit(-1);
        }

        final Matcher matcher = new Matcher(args[0]);

        String result = matcher.match(args[1]) ? "matched" : "not-matched";
        System.out.println(result);
    }
}


