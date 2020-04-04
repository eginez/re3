package xyz.eginez.re3;

import org.junit.Assert;
import org.junit.Test;

public class MatcherTest {
    @Test
    public void testParsing() {
        Matcher m = new Matcher("a*");
        final State start = m.getStart();
        Assert.assertTrue(start.isSplit());
        Assert.assertTrue(start.next.isNoop());
        Assert.assertEquals(State.MATCHED, start.next2);

        Assert.assertTrue(m.match("a"));
        Assert.assertTrue(m.match("aa"));
        Assert.assertTrue(m.match(""));
    }

    @Test
    public void testSimpleConcat() {
        Matcher m = new Matcher("ab");
        Assert.assertTrue(m.match("abc"));

        Assert.assertTrue(m.match("ab"));
        Assert.assertFalse(m.match("b"));
        Assert.assertFalse(m.match(""));
        Assert.assertFalse(m.match("a"));
        Assert.assertTrue(m.match("aba"));
    }
}
