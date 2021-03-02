package ru.hse.subbotina.java.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.hse.subbotina.java.functional.Function1;

public class TestFunction1 {

    private final Integer toAddFst = 10;
    private final Integer toSubSnd = 3;
    private final Integer pos = 0;
    private final Character charEq = 'a';
    private Function1<Integer, Integer> funIntIntFst;
    private Function1<Integer, Integer> funIntIntSnd;
    private Function1<String, Character> funCharStrFst;
    private Function1<Character, Boolean> funCharStrSnd;

    private void initIntInt() {
        funIntIntFst = var -> var + toAddFst;
        funIntIntSnd = var -> var - toSubSnd;
    }

    private void initCharStr() {
        funCharStrFst = var -> var.charAt(pos);
        funCharStrSnd = var -> var.equals(charEq);
    }

    @Test
    public void testApplyInt() {
        initIntInt();
        assertEquals(10, funIntIntFst.apply(0));
        assertEquals(7, funIntIntFst.apply(-3));
        assertEquals(-3, funIntIntSnd.apply(0));
        assertEquals(97, funIntIntSnd.apply(100));
    }

    @Test
    public void testApplyCharStr() {
        initCharStr();
        assertEquals('h', funCharStrFst.apply("hello"));
        assertEquals('!', funCharStrFst.apply("!!!"));
        assertEquals(true, funCharStrSnd.apply('a'));
        assertEquals(false, funCharStrSnd.apply('b'));
    }

    @Test
    public void testComposeInt() {
        initIntInt();
        Function1<Integer, Integer> res = funIntIntFst.compose(funIntIntSnd);
        assertEquals(8, res.apply(1));
        assertEquals(17, res.apply(10));
    }

    @Test
    public void testComposeCharStr() {
        initCharStr();
        Function1<String, Boolean> res = funCharStrFst.compose(funCharStrSnd);
        assertFalse(res.apply("hello"));
        assertTrue(res.apply("aello"));
    }
}
