package xyz.eginez.re3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VM {
    static class Runtime {
        final private List<Instruction> program;
        private List<Instruction> current;
        private List<Instruction> next;

        public Runtime(List<Instruction> program, int pc) {
            this.program = program;
            this.current = Collections.singletonList(program.get(pc));
            this.next = new ArrayList<>();
        }

        void add(int... pcs) {
            for (int p : pcs) {
                next.add(program.get(p));
            }
        }

        void next(Instruction instruction) {
            int i = program.indexOf(instruction);
            assert i != -1;
            next.add(program.get(i + 1));
        }

        List<Instruction> getCurrent() {
            return current;
        }

        void tick() {
            current = next;
            next = new ArrayList<>();
        }
    }

    public boolean interpret(final List<Instruction> program, String input) {
        Runtime rt = new Runtime(program, 0);
        int sp = 0;
        while (!rt.getCurrent().isEmpty()) {
            int c = sp < input.length()
                    ? input.charAt(sp++)
                    : 0;
            for (Instruction ins : rt.getCurrent()) {
                if (ins.eval(c, rt)) {
                    return true;
                }
            }
            rt.tick();
        }
        return false;
    }
}
