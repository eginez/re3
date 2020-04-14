package xyz.eginez.re3;

import org.junit.Test;

import java.util.List;
import static org.junit.Assert.assertTrue;


public class MatcherVMTest {
    @Test
    public void testVM() {
        MatcherVM m = new MatcherVM("a*b*");
        List<Instruction> instructions = m.toByteCode();
        System.out.println(MatcherVM.instructions(instructions));
        VM vm = new VM();
       // assertTrue(vm.interpret(instructions, "ab"));
    }
}
