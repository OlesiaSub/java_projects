package ru.hse.subbotina.java.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.hse.subbotina.java.functional.Function1;
import ru.hse.subbotina.java.functional.Function2;

import java.util.Locale;

public class TestFunction2 {

    private final Integer toAddInt = 5;
    private final Integer toBindInt = 3;
    private final String toBindStr = "Hello";
    private final Character toBindChar = '!';

    private Function1<Integer, Integer> funIntIntFst;
    private Function2<Integer, Integer, Integer> funIntIntIntSnd;
    private Function1<String, String> funStrStrFst;
    private Function2<String, Character, String> funStrCharStrSnd;

    private void initInt() {
        funIntIntFst = var -> var + toAddInt;
        funIntIntIntSnd = (var1, var2) -> var1 * var2;
    }

    private void initCharStr() {
        funStrStrFst = var -> var.toLowerCase(Locale.ROOT);
        funStrCharStrSnd = (var1, var2) -> var1 + var2;
    }

    @Test
    public void testComposeInt() {
        initInt();
        Function2<Integer, Integer, Integer> res = funIntIntIntSnd.compose(funIntIntFst);
        assertEquals(5, res.apply(0, 0));
        assertEquals(2, res.apply(3, -1));
    }

    @Test
    public void testComposeCharStr() {
        initCharStr();
        Function2<String, Character, String> res = funStrCharStrSnd.compose(funStrStrFst);
        assertEquals("hello!", res.apply("Hello", '!'));
        assertEquals("!", res.apply("", '!'));
    }

    @Test
    public void testBind1Int() {
        initInt();
        Function1<Integer, Integer> res = funIntIntIntSnd.bind1(toBindInt);
        assertEquals(0, res.apply(0));
        assertEquals(3, res.apply(1));
        assertEquals(6, res.apply(2));
    }
    
    @Test
    public void testBind1CharStr() {
        initCharStr();
        Function1<Character, String> res = funStrCharStrSnd.bind1(toBindStr);
        assertEquals("Hello!", res.apply('!'));
        assertEquals("HelloO", res.apply('O'));
    }

    @Test
    public void testBind2Int() {
        initInt();
        Function1<Integer, Integer> res = funIntIntIntSnd.bind2(toBindInt);
        assertEquals(0, res.apply(0));
        assertEquals(3, res.apply(1));
        assertEquals(6, res.apply(2));
    }

    @Test
    public void testBind2CharStr() {
        initCharStr();
        Function1<String, String> res = funStrCharStrSnd.bind2(toBindChar);
        assertEquals("Hello!", res.apply("Hello"));
        assertEquals("java!", res.apply("java"));
    }

    @Test
    public void testCurryInt() {
        initInt();
        Function1<Integer, Integer> res = funIntIntIntSnd.curry(toBindInt);
        assertEquals(0, res.apply(0));
        assertEquals(3, res.apply(1));
        assertEquals(6, res.apply(2));
    }

    @Test
    public void testCurryCharStr() {
        initCharStr();
        Function1<String, String> res = funStrCharStrSnd.curry(toBindChar);
        assertEquals("Hello!", res.apply("Hello"));
        assertEquals("java!", res.apply("java"));
    }
}
