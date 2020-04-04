package xyz.eginez.re3;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatcherTest {
    @Test
    public void testParsing() {
        Matcher m = new Matcher("a*");
        final State start = m.getStart();
        Assert.assertTrue(start.isSplit());
        Assert.assertTrue(start.next.isNoop());
        Assert.assertEquals(State.MATCHED, start.next2);

        Assert.assertTrue(m.matchAny("a"));
        Assert.assertTrue(m.matchAny("aa"));
        Assert.assertTrue(m.matchAny(""));
    }

    @Test
    public void testSimpleConcat() {
        Matcher m = new Matcher("ab");
        Assert.assertTrue(m.matchAny("abc"));
        Assert.assertTrue(m.matchAny("ab"));
        Assert.assertFalse(m.matchAny("b"));
        Assert.assertFalse(m.matchAny(""));
        Assert.assertFalse(m.matchAny("a"));
        Assert.assertTrue(m.matchAny("aba"));
    }

    @Test
    public void testSimpleOneOrMore() {
        Matcher matcher = new Matcher("a+");
        assertTrue(matcher.matchAny("a"));
        assertTrue(matcher.matchAny("aa"));
        assertFalse(matcher.matchAny(""));
        assertFalse(matcher.matchAny("b"));
    }

    @Test
    public void testSimpleZeroOrOne() {
        Matcher matcher = new Matcher("a?");
        assertTrue(matcher.matchAny("a"));
        assertTrue(matcher.matchAny("b"));
        assertTrue(matcher.matchAny(""));
        assertTrue(matcher.matchAny("aa"));
    }

    @Test
    public void simpleAlternate() {
        Matcher matcher = new Matcher("ab|");
        assertTrue(matcher.matchAny("b"));
        assertTrue(matcher.matchAny("a"));
    }
}
