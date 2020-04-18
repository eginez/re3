package xyz.eginez.re3;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class MatcherVMTest {

    private static void match(MatcherVM m, boolean match, String ...args) {
        VM vm = new VM();
        List<Instruction> instructions = m.toByteCode();
        for (String s : args) {
            boolean res = vm.interpret(instructions, s);
            if (res == match) {
                assertTrue(true);
            } else {
                System.out.println(MatcherVM.instructions(instructions));
                fail(String.format("%s did not NOT match %s", s, m.getRegex()));
            }
        }
    }

    @Test
    public void testVM() {
        MatcherVM m = new MatcherVM("a*b*");
        List<Instruction> instructions = m.toByteCode();
        System.out.println(MatcherVM.instructions(instructions));
        VM vm = new VM();
        assertTrue(vm.interpret(instructions, "ab"));
    }

    @Test
    public void testParOne() {
        MatcherVM m = new MatcherVM("a(bc)*");
        match(m, true, "abc");
        match(m, true, "a");
        match(m, true, "ab");

        m = new MatcherVM("a(bc)*d");
        match(m, true, "ad");

    }
}
