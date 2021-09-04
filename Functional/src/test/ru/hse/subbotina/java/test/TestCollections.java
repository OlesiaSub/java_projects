package ru.hse.subbotina.java.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.hse.subbotina.java.collection.Collections;
import ru.hse.subbotina.java.functional.Function1;
import ru.hse.subbotina.java.functional.Function2;
import ru.hse.subbotina.java.predicate.Predicate;

import java.util.Arrays;

public class TestCollections {

    private Function2<String, Character, String> funStrCharStrSnd;

    @Test
    public void testCollectionsMapIntInt() {
        Function1<Integer, Integer> fun = var -> var + 5;
        Iterable<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> res = Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        assertEquals(res, Collections.map(fun, list));
    }

    @Test
    public void testCollectionsMapCharStr() {
        Function1<String, Character> fun = var -> var.charAt(0);
        Iterable<String> list = Arrays.asList("Hello", "world", "java", "functional","!");
        Iterable<Character> res = Arrays.asList('H', 'w','j','f','!');
        assertEquals(res, Collections.map(fun, list));
    }

    @Test
    public void testCollectionsFilterIntInt() {
        Predicate<Integer> predicate = var -> var % 2 == 0;
        Iterable<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> res = Arrays.asList(0, 2, 4, 6, 8, 10);
        assertEquals(res, Collections.filter(predicate, list));
    }

    @Test
    public void testCollectionsFilterStr() {
        Predicate<String> predicate = var -> var.matches("[a-z]*");
        Iterable<String> list = Arrays.asList("Hello", "world", "java", "funcTionAl","!?");
        Iterable<String> res = Arrays.asList("world", "java");
        assertEquals(res, Collections.filter(predicate, list));
    }

    @Test
    public void testCollectionsTakeWhileInt() {
        Predicate<Integer> predicate = var -> var < 5;
        Iterable<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> res = Arrays.asList(0, 1, 2, 3, 4);
        assertEquals(res, Collections.takeWhile(predicate, list));
    }

    @Test
    public void testCollectionsTakeWhileStr() {
        Predicate<String> predicate = var -> var.matches("[a-z]*");
        Iterable<String> list = Arrays.asList("hello", "world", "java", "funcTionAl","!?");
        Iterable<String> res = Arrays.asList("hello", "world", "java");
        assertEquals(res, Collections.takeWhile(predicate, list));
    }

    @Test
    public void testCollectionsTakeUnlessIntInt() {
        Predicate<Integer> predicate = var -> var >= 5;
        Iterable<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Iterable<Integer> res = Arrays.asList(0, 1, 2, 3, 4);
        assertEquals(res, Collections.takeUnless(predicate, list));
    }

    @Test
    public void testCollectionsUnlessWhileStr() {
        Predicate<String> predicate = var -> var.matches("[a-z]*");
        Iterable<String> list = Arrays.asList("Hello", "WORLD", "java", "funcTionAl","!?");
        Iterable<String> res = Arrays.asList("Hello", "WORLD");
        assertEquals(res, Collections.takeUnless(predicate, list));
    }

    @Test
    public void testCollectionsFoldrNonAssociativeIntInt() {
        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Function2<Integer, Integer, Integer> fun = (var1, var2) -> var1 - var2;
        assertEquals(-7, Collections.foldr(fun, 10, list));
    }

    @Test
    public void testCollectionsFoldrAssociativeIntInt() {
        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Function2<Integer, Integer, Integer> fun = (var1, var2) -> var1 * var2;
        assertEquals(120, Collections.foldr(fun, 1, list));
    }

    @Test
    public void testCollectionsFoldrCharStr() {
        Iterable<Character> list = Arrays.asList('H', 'e','l','l','o');
        Function2<Character, String, String> fun = (str, ch) -> str + ch;
        assertEquals("Hello, world!", Collections.foldr(fun, ", world!", list));
    }

    @Test
    public void testCollectionsFoldlNonAssociativeIntInt() {
        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Function2<Integer, Integer, Integer> fun = (var1, var2) -> var1 - var2;
        assertEquals(-5, Collections.foldl(fun, 10, list));
    }

    @Test
    public void testCollectionsFoldlAssociativeIntInt() {
        Iterable<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Function2<Integer, Integer, Integer> fun = (var1, var2) -> var1 * var2;
        assertEquals(120, Collections.foldl(fun, 1, list));
    }

    @Test
    public void testCollectionsFoldlCharStr() {
        Iterable<Character> list = Arrays.asList('h', 'e','l','l','o', '!');
        Function2<String, Character, String> fun = (str, ch) -> str + ch;
        assertEquals("World, hello!", Collections.foldl(fun, "World, ", list));
    }
}
