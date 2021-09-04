package ru.hse.subbotina.java.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.hse.subbotina.java.predicate.Predicate;

public class TestPredicate {
    private Predicate<Integer> intPredGreaterThanZero;
    private Predicate<Integer> intPredEven;
    private Predicate<String> strPredMatch;
    private Predicate<String> strPredLen;

    private void initInt() {
        intPredGreaterThanZero = var -> var > 0;
        intPredEven = var -> var % 2 == 0;
    }
    
    private void initStr() {
        strPredMatch = var -> var.charAt(0) == 'h';
        strPredLen = var -> var.length() < 7;
    }

    @Test
    public void testPredIntOr() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.or(intPredEven);
        assertTrue(res.apply(6));
        assertTrue(res.apply(122));
        assertTrue(res.apply(0));
        assertTrue(res.apply(-4));
        assertTrue(res.apply(7));
        assertFalse(res.apply(-7));
    }
    
    @Test
    public void testPredStrOr() {
        initStr();
        Predicate<String> res = strPredLen.or(strPredMatch);
        assertTrue(res.apply("hello"));
        assertTrue(res.apply("hELLO!"));
        assertTrue(res.apply("Hello"));
        assertFalse(res.apply("HELLO!!!"));
        assertFalse(res.apply("mello, World!"));
    } 

    @Test
    public void testPredIntAnd() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.and(intPredEven);
        assertTrue(res.apply(6));
        assertTrue(res.apply(122));
        assertFalse(res.apply(0));
        assertFalse(res.apply(-4));
        assertFalse(res.apply(7));
        assertFalse(res.apply(-7));
    }

    @Test
    public void testPredIntAndNot() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.and(intPredEven.not());
        assertFalse(res.apply(6));
        assertFalse(res.apply(122));
        assertFalse(res.apply(0));
        assertFalse(res.apply(-4));
        assertTrue(res.apply(7));
        assertFalse(res.apply(-7));
    }

    @Test
    public void testPredStrAnd() {
        initStr();
        Predicate<String> res = strPredLen.and(strPredMatch);
        assertTrue(res.apply("hello"));
        assertFalse(res.apply("Hello"));
        assertFalse(res.apply("HELLO!"));
        assertFalse(res.apply("HELLO!!!"));
        assertFalse(res.apply("hello, World!"));
    }

    @Test
    public void testPredIntNotGreaterThanZero() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.not();
        assertFalse(res.apply(2));
        assertTrue(res.apply(0));
        assertTrue(res.apply(-3));
    }

    @Test
    public void testPredIntNotIsEven() {
        initInt();
        Predicate<Integer> res = intPredEven.not();
        assertFalse(res.apply(22));
        assertFalse(res.apply(-8));
        assertFalse(res.apply(0));
        assertTrue(res.apply(7));
        assertTrue(res.apply(-3));
    }

    @Test
    public void testPredStrNotLength() {
        initStr();
        Predicate<String> res = strPredLen.not();
        assertTrue(res.apply("hellooooo"));
        assertFalse(res.apply("hello"));
        assertFalse(res.apply(""));
    }

    @Test
    public void testPredStrNotMatch() {
        initStr();
        Predicate<String> res = strPredMatch.not();
        assertFalse(res.apply("hellooooo"));
        assertFalse(res.apply("hello"));
        assertTrue(res.apply("java"));
    }

    @Test
    public void testPredAlwaysTrue() {
        initInt();
        Predicate<Integer> res = Predicate.ALWAYS_TRUE();
        assertTrue(res.apply(10));
        assertTrue(res.apply(0));
        assertTrue(res.apply(-23));
        assertTrue(res.apply(13));
    }

    @Test
    public void testPredIntAlwaysTrueWithOr() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.or(Predicate.ALWAYS_TRUE());
        assertTrue(res.apply(6));
        assertTrue(res.apply(122));
        assertTrue(res.apply(0));
        assertTrue(res.apply(-4));
        assertTrue(res.apply(7));
        assertTrue(res.apply(-7));
    }

    @Test
    public void testPredStrAlwaysTrue() {
        initStr();
        Predicate<String> res = Predicate.ALWAYS_TRUE();
        assertTrue(res.apply("helo"));
        assertTrue(res.apply("!!!"));
        assertTrue(res.apply(""));
    }

    @Test
    public void testPredAlwaysFalse() {
        initInt();
        Predicate<Integer> res = Predicate.ALWAYS_FALSE();
        assertFalse(res.apply(10));
        assertFalse(res.apply(0));
        assertFalse(res.apply(-23));
        assertFalse(res.apply(13));
    }

    @Test
    public void testPredIntAlwaysFalseWithAnd() {
        initInt();
        Predicate<Integer> res = intPredGreaterThanZero.and(Predicate.ALWAYS_FALSE());
        assertFalse(res.apply(6));
        assertFalse(res.apply(122));
        assertFalse(res.apply(0));
        assertFalse(res.apply(-4));
        assertFalse(res.apply(7));
        assertFalse(res.apply(-7));
    }

    @Test
    public void testPredStrAlwaysFalse() {
        initStr();
        Predicate<String> res = Predicate.ALWAYS_FALSE();
        assertFalse(res.apply("helo"));
        assertFalse(res.apply("!!!"));
        assertFalse(res.apply(""));
    }

}
