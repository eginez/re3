package xyz.eginez.re3;

import java.util.List;
import java.util.Set;

class State {
    char c;
    State exit;
    State exit2;

    static final State MATCHED_STATE = new State();
}

class Fragment {
    State start;
    List<State> outStates;
}

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong arguments.Expected a regex and a string");
            System.exit(-1);
        }

        final Fragment startFragment = parseRegex(args[0]);
        final Set<State> finalStates = execute(startFragment, args[1]);

        String result = finalStates.contains(State.MATCHED_STATE) ? "matched" : "not-matched";
        System.out.println(result);
    }

    private static Set<State> execute(Fragment startFragment, String string) {
        return null;
    }

    private static Fragment parseRegex(String arg) {
        return null;
    }
}


