package xyz.eginez.re3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

class State {
    char c;
    State exit;
    State exit2;

    public static final State MATCHED_STATE = new State('\0', null, null);
    public static final char SPLIT = '\r';

    State(char c, State exit, State exit2) {
        this.c = c;
        this.exit = exit;
        this.exit2 = exit2;
    }

    void connect(State s) {
        exit = exit == null ? s : exit;
        exit2 = exit2 == null ? s : exit2;
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

    private static State parseRegex(String regex) {
        Stack<Fragment> stack = new Stack<>();

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            Fragment fragment;
            switch (c) {
                case '*':
                    Fragment f = stack.pop();
                    State s = new State(State.SPLIT, f.start, null);
                    fragment = new Fragment(s, s);
                    f.connect(s);
                    break;
                default:
                    State st = new State(c, null, null);
                    fragment = new Fragment(st, st);
                    break;
            }
            stack.push(fragment);
        }

        final Fragment whole = stack.pop();
        whole.connect(State.MATCHED_STATE);
        return whole.start;
    }

    public boolean match(String string) {
        Set<State> currStates = new HashSet<>();
        addState(currStates, start);

        for (int i = 0; i < string.length(); i++) {
            currStates = step(currStates, string.charAt(i));
        }
        return currStates.contains(State.MATCHED_STATE);
    }

    private static void addState(Set<State> acc, State s) {
        if (s == null || acc.contains(s)) {
            return;
        }

        if (s.c == State.SPLIT) {
            addState(acc, s.exit);
            addState(acc, s.exit2);
            return;
        }
        acc.add(s);
    }

    private static Set<State> step(Set<State> currentStates, char c) {
        HashSet<State> newStates = new HashSet<>();
        currentStates.forEach(s -> {
            if (s.c == c) {
                addState(newStates, s.exit);
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


