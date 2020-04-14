package xyz.eginez.re3;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatcherTest {

    private static void match(Matcher m, boolean match, String ...args) {
        for (String s : args) {
            if (match) {
                assertTrue(String.format("%s should match %s", s, m.getRegex()), m.matchAny(s));
            } else {
                assertFalse(String.format("%s should NOT match %s", s, m.getRegex()), m.matchAny(s));
            }
        }
    }

    @Test
    public void testParsing() {
        Matcher m = new Matcher("a*b");
        final State start = m.getStart();
        Assert.assertTrue(start.isSplit());

        Assert.assertTrue(m.matchAny("ab"));
        Assert.assertTrue(m.matchAny("aab"));
        Assert.assertTrue(m.matchAny("b"));
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
        assertFalse(matcher.matchAny(""));
        assertTrue(matcher.matchAny("ab"));
    }


    @Test
    public void mixRegex() {
        Matcher m = new Matcher("es|teban*");
        match(m, true, "steban", "etebann");
    }
}
