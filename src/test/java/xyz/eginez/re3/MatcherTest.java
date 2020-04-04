package xyz.eginez.re3;

import org.junit.Assert;
import org.junit.Test;

public class MatcherTest {
    @Test
    public void testParsing() {
        Matcher m = new Matcher("a*");
        final State start = m.getStart();
        Assert.assertEquals(State.SPLIT, start.c);
        Assert.assertEquals('a', start.exit.c);
        Assert.assertEquals(State.MATCHED_STATE, start.exit2);

        Assert.assertTrue(m.match("a"));
        Assert.assertTrue(m.match("aa"));
        Assert.assertTrue(m.match(""));
    }

    @Test
    public void testSimpleZeroOrMore() {
        //Missing concatenation
        Matcher m = new Matcher("ab*");
        Assert.assertTrue(m.match("a"));
        Assert.assertTrue(m.match("ab"));
        Assert.assertFalse(m.match(""));
    }
}
