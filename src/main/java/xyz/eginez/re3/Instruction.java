package xyz.eginez.re3;

public abstract class Instruction {
    public static final int MATCH_OPCODE = 0;
    public static final int CHAR_OPCODE = 100;
    public static final int SPLIT_OPCODE = 101;
    public static final int JUMP_OPCODE = 102;

    int opCode;
    int operand1;
    int operand2;

    protected Instruction(int opCode, int operand1, int operand2) {
        this.opCode = opCode;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public abstract boolean eval(int input, VM.Runtime runtime);
}

class CharInstruction extends Instruction {
    public CharInstruction(int c) {
        super(CHAR_OPCODE, c, 0);
    }

    @Override
    public boolean eval(int input, VM.Runtime rt) {
        if (input == operand1) {
            rt.next(this);
        }
        return  false;
    }

    @Override
    public String toString() {
        return "char " + (char) operand1;
    }

}

class JumpInstruction extends Instruction {

    public JumpInstruction(int jump) {
        super(JUMP_OPCODE, jump, 0);
    }

    @Override
    public boolean eval(int input, VM.Runtime rt) {
        rt.add(operand1);
        return false;
    }
    @Override
    public String toString() {
        return String.format("jump %d", operand1);
    }
}

class SplitInstruction extends Instruction {
    public SplitInstruction(int left, int right) {
        super(SPLIT_OPCODE, left, right);
    }

    @Override
    public boolean eval(int input, VM.Runtime rt) {
        rt.add(operand1, operand2);
        return false;
    }

    @Override
    public String toString() {
        return String.format("split %d %d", operand1, operand2);
    }
}

class MatchInstruction extends Instruction {
    public MatchInstruction() {
        super(MATCH_OPCODE, 0, 0);
    }

    @Override
    public boolean eval(int input, VM.Runtime rt) {
        return true;
    }

    @Override
    public String toString() {
        return "match";
    }
}
