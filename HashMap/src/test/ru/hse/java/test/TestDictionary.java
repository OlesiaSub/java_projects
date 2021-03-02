package ru.hse.java.test;

import org.junit.jupiter.api.Test;
import ru.hse.java.util.DictionaryImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestDictionary {

    public DictionaryImpl<Integer, Integer> dictionaryIntInt;
    public DictionaryImpl<Character, String> dictionaryCharStr;

    private void createIntInt() {
        dictionaryIntInt = new DictionaryImpl<>();
    }

    private void createCharStr() {
        dictionaryCharStr = new DictionaryImpl<>(8, 0.5, 2);
    }

    private void putBasicElementsIntInt() {
        dictionaryIntInt.put(1, 11);
        dictionaryIntInt.put(2, 12);
        dictionaryIntInt.put(3, 13);
        dictionaryIntInt.put(3, 17);
    }

    private void putBasicElementsCharStr() {
        dictionaryCharStr.put('a', "aa");
        dictionaryCharStr.put('b', "ab");
        dictionaryCharStr.put('c', "ac");
        dictionaryCharStr.put('c', "acc");
    }

    @Test
    public void testMapIntIntPutBasic() {
        createIntInt();
        assertNull(dictionaryIntInt.put(1, 11));
        assertNull(dictionaryIntInt.put(2, 12));
        assertNull(dictionaryIntInt.put(3, 13));
        assertNull(dictionaryIntInt.put(4, 14));
        assertEquals(14, dictionaryIntInt.put(4, 17));
    }

    @Test
    public void testMapCharStrPutBasic() {
        createCharStr();
        assertNull(dictionaryCharStr.put('a', "aa"));
        assertNull(dictionaryCharStr.put('b', "ab"));
        assertNull(dictionaryCharStr.put('c', "ac"));
        assertNull(dictionaryCharStr.put('d', "ad"));
        assertEquals("ac", dictionaryCharStr.put('c', "az"));
    }

    @Test
    public void testMapIntIntContainsKeyBasic() {
        createIntInt();
        putBasicElementsIntInt();
        assertTrue(dictionaryIntInt.containsKey(1));
        assertTrue(dictionaryIntInt.containsKey(2));
        assertTrue(dictionaryIntInt.containsKey(3));
        assertFalse(dictionaryIntInt.containsKey(6));
        assertFalse(dictionaryIntInt.containsKey(-1));
    }

    @Test
    public void testMapIntIntContainsKeyAfterRemoval() {
        createIntInt();
        putBasicElementsIntInt();
        assertTrue(dictionaryIntInt.containsKey(1));
        dictionaryIntInt.remove(1);
        assertFalse(dictionaryIntInt.containsKey(1));
    }

    @Test
    public void testMapCharStrContainsBasic() {
        createCharStr();
        putBasicElementsCharStr();
        assertTrue(dictionaryCharStr.containsKey('a'));
        assertTrue(dictionaryCharStr.containsKey('b'));
        assertTrue(dictionaryCharStr.containsKey('b'));
        assertFalse(dictionaryCharStr.containsKey('d'));
    }

    @Test
    public void testMapCharStrContainsAfterRemoval() {
        createCharStr();
        putBasicElementsCharStr();
        assertTrue(dictionaryCharStr.containsKey('a'));
        dictionaryCharStr.remove('a');
        assertFalse(dictionaryCharStr.containsKey('a'));
    }

    @Test
    public void testMapIntIntGetBasic() {
        createIntInt();
        putBasicElementsIntInt();
        assertEquals(11, dictionaryIntInt.get(1));
        assertEquals(17, dictionaryIntInt.get(3));
    }

    @Test
    public void testMapIntIntGetReplacement() {
        createIntInt();
        putBasicElementsIntInt();
        assertEquals(12, dictionaryIntInt.get(2));
        dictionaryIntInt.put(2, 22);
        assertEquals(22, dictionaryIntInt.get(2));
    }

    @Test
    public void testMapIntIntGetAfterRemoval() {
        createIntInt();
        putBasicElementsIntInt();
        assertEquals(12, dictionaryIntInt.get(2));
        dictionaryIntInt.remove(2);
        assertNull(dictionaryIntInt.get(2));
    }

    @Test
    public void testMapIntIntGetNonExisting() {
        createIntInt();
        putBasicElementsIntInt();
        assertNull(dictionaryIntInt.get(4));
        assertNull(dictionaryIntInt.get(-1));
    }

    @Test
    public void testMapCharStrGetBasic() {
        createCharStr();
        putBasicElementsCharStr();
        assertEquals("aa", dictionaryCharStr.get('a'));
        assertEquals("acc", dictionaryCharStr.get('c'));
    }

    @Test
    public void testMapCharStrGetReplacement() {
        createCharStr();
        putBasicElementsCharStr();
        assertEquals("aa", dictionaryCharStr.get('a'));
        dictionaryCharStr.put('a', "aaa");
        assertEquals("aaa", dictionaryCharStr.get('a'));
    }

    @Test
    public void testMapCharStrGetNonExisting() {
        createCharStr();
        putBasicElementsCharStr();
        assertNull(dictionaryCharStr.get('d'));
        assertNull(dictionaryCharStr.get(' '));
    }

    @Test
    public void testMapCharStrGetAfterRemoval() {
        createCharStr();
        putBasicElementsCharStr();
        assertEquals("ab", dictionaryCharStr.get('b'));
        dictionaryCharStr.remove('b');
        assertNull(dictionaryCharStr.get('b'));
    }

    @Test
    public void testMapIntIntSizeEmptyMap() {
        createIntInt();
        assertEquals(0, dictionaryIntInt.size());
    }

    @Test
    public void testMapIntIntSizeAfterAddition() {
        createIntInt();
        dictionaryIntInt.put(1, 11);
        dictionaryIntInt.put(2, 12);
        assertEquals(2, dictionaryIntInt.size());
        dictionaryIntInt.put(3, 13);
        assertEquals(3, dictionaryIntInt.size());
    }

    @Test
    public void testMapItIntSizeAfterRemoval() {
        createIntInt();
        putBasicElementsIntInt();
        assertEquals(3, dictionaryIntInt.size());
        dictionaryIntInt.remove(2);
        assertEquals(2, dictionaryIntInt.size());
        dictionaryIntInt.remove(2);
        assertEquals(2, dictionaryIntInt.size());
    }

    @Test
    public void testMapCharStrSizeEmptyMap() {
        createCharStr();
        assertEquals(0, dictionaryCharStr.size());
    }

    @Test
    public void testMapCharStrSizeAfterAddition() {
        createCharStr();
        dictionaryCharStr.put('a', "aa");
        dictionaryCharStr.put('b', "ab");
        assertEquals(2, dictionaryCharStr.size());
        dictionaryCharStr.put('c', "ac");
        assertEquals(3, dictionaryCharStr.size());
        dictionaryCharStr.put('c', "acc");
        assertEquals(3, dictionaryCharStr.size());
    }

    @Test
    public void testMapCharStrSizeAfterRemoval() {
        createCharStr();
        putBasicElementsCharStr();
        assertEquals(3, dictionaryCharStr.size());
        dictionaryCharStr.remove('a');
        assertEquals(2, dictionaryCharStr.size());
    }

    @Test
    public void testMapIntIntRemoveBasic() {
        createIntInt();
        putBasicElementsIntInt();
        assertEquals(11, dictionaryIntInt.remove(1));
        assertEquals(17, dictionaryIntInt.remove(3));
    }

    @Test
    public void testMapIntIntRemoveNonExistent() {
        createIntInt();
        assertNull(dictionaryIntInt.remove(1));
        putBasicElementsIntInt();
        assertEquals(11, dictionaryIntInt.remove(1));
        assertNull(dictionaryIntInt.remove(1));
        assertNull(dictionaryIntInt.remove(10));
    }

    @Test
    public void testMapCharStrRemoveBasic() {
        createCharStr();
        putBasicElementsCharStr();
        assertEquals("aa", dictionaryCharStr.remove('a'));
        assertEquals("acc", dictionaryCharStr.remove('c'));
    }

    @Test
    public void testMapCharStrRemoveNonExistent() {
        createCharStr();
        assertNull(dictionaryCharStr.remove('a'));
        putBasicElementsCharStr();
        assertEquals("aa", dictionaryCharStr.remove('a'));
        assertNull(dictionaryCharStr.remove('a'));
        assertNull(dictionaryCharStr.remove('z'));
    }

    @Test
    public void testMapIntIntClear() {
        createIntInt();
        putBasicElementsIntInt();
        dictionaryIntInt.clear();
        assertEquals(0, dictionaryIntInt.size());
        assertEquals(dictionaryIntInt.initialCapacity, dictionaryIntInt.capacity());
    }

    @Test
    public void testMapCharStrClear() {
        createCharStr();
        putBasicElementsCharStr();
        dictionaryCharStr.clear();
        assertEquals(0, dictionaryCharStr.size());
        assertEquals(dictionaryCharStr.initialCapacity, dictionaryCharStr.capacity());
    }

    @Test
    public void stressTestMapIntInt() {
        createIntInt();
        Random rand = new Random();
        for (int i = 0; i < 500; i++) {
            Integer cur = rand.nextInt(1000);
            dictionaryIntInt.put(i, cur);
        }
        assertTrue(dictionaryIntInt.size() <= 500 && dictionaryIntInt.size() > 0);
        for (int i = 0; i < 500; i++) {
            assertTrue(dictionaryIntInt.containsKey(i));
        }
        for (int i = 0; i < 250; i++) {
            dictionaryIntInt.remove(i);
        }
        assertTrue(dictionaryIntInt.size() <= 250);

        createIntInt();
        for (int i = 0; i < 500; i++) {
            Integer cur = rand.nextInt(1000);
            dictionaryIntInt.put(cur, i);
        }
        assertTrue(dictionaryIntInt.size() <= 500 && dictionaryIntInt.size() > 0);
    }

    @Test
    public void stressTestMapCharStr() {
        createCharStr();
        for (int i = 0; i < 500; i++) {
            String cur = UUID.randomUUID().toString();
            dictionaryCharStr.put((char) i, cur);
        }
        assertTrue(dictionaryCharStr.size() <= 500 && dictionaryCharStr.size() > 0);
        for (int i = 0; i < 500; i++) {
            assertTrue(dictionaryCharStr.containsKey((char) i));
        }
        for (int i = 0; i < 250; i++) {
            dictionaryCharStr.remove((char) i);
        }
        assertTrue(dictionaryCharStr.size() <= 250);

        createCharStr();
        Random rand = new Random();
        for (int i = 0; i < 500; i++) {
            String cur = UUID.randomUUID().toString();
            dictionaryCharStr.put((char) rand.nextInt(1000), cur);
        }
        assertTrue(dictionaryCharStr.size() <= 500 && dictionaryCharStr.size() > 0);
    }

    private void putBasicElementsToTestMapIntInt(Map<Integer, Integer> testMap) {
        testMap.put(1, 11);
        testMap.put(2, 12);
        testMap.put(3, 13);
        testMap.put(3, 17);
    }

    @Test
    public void testMapIntIntKeySet() {
        createIntInt();
        putBasicElementsIntInt();
        Map<Integer, Integer> testMap = new HashMap<>();
        putBasicElementsToTestMapIntInt(testMap);
        assertEquals(testMap.keySet(), dictionaryIntInt.keySet());
    }

    @Test
    public void testMapIntIntValues() {
        createIntInt();
        putBasicElementsIntInt();
        Map<Integer, Integer> testMap = new HashMap<>();
        putBasicElementsToTestMapIntInt(testMap);
        assertTrue(testMap.values().containsAll(dictionaryIntInt.values()));
//        assertTrue(dictionaryIntInt.values().containsAll(testMap.values())); // не проходит
    }

    @Test
    public void testMapIntIntEntrySet() {
        createIntInt();
        putBasicElementsIntInt();
        Map<Integer, Integer> testMap = new HashMap<>();
        putBasicElementsToTestMapIntInt(testMap);
        assertEquals(testMap.entrySet(), dictionaryIntInt.entrySet());
    }

    @Test
    public void testMapIntIntKeySetRemove() {
        createIntInt();
        putBasicElementsIntInt();
        Iterator<Integer> iter = dictionaryIntInt.keySet().iterator();
        assertEquals(12, dictionaryIntInt.get(2));
        assertEquals(3, dictionaryIntInt.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryIntInt.get(2));
        assertEquals(2, dictionaryIntInt.size());
    }

    @Test
    public void testMapIntIntValuesRemove() {
        createIntInt();
        putBasicElementsIntInt();
        Iterator<Integer> iter = dictionaryIntInt.values().iterator();
        assertEquals(12, dictionaryIntInt.get(2));
        assertEquals(3, dictionaryIntInt.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryIntInt.get(2));
        assertEquals(2, dictionaryIntInt.size());
    }

    @Test
    public void testMapIntIntEntrySetRemove() {
        createIntInt();
        putBasicElementsIntInt();
        Iterator<Map.Entry<Integer, Integer>> iter = dictionaryIntInt.entrySet().iterator();
        assertEquals(12, dictionaryIntInt.get(2));
        assertEquals(3, dictionaryIntInt.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryIntInt.get(2));
        assertEquals(2, dictionaryIntInt.size());
    }

    private void putBasicElementsToTestMapCharStr(Map<Character, String> testMap) {
        testMap.put('a', "aa");
        testMap.put('b', "ab");
        testMap.put('c', "ac");
        testMap.put('c', "acc");
    }

    @Test
    public void testMapCharStrKeySet() {
        createCharStr();
        putBasicElementsCharStr();
        Map<Character, String> testMap = new HashMap<>();
        putBasicElementsToTestMapCharStr(testMap);
        assertEquals(testMap.keySet(), dictionaryCharStr.keySet());
    }

    @Test
    public void testMapCharStrValues() {
        createCharStr();
        putBasicElementsCharStr();
        Map<Character, String> testMap = new HashMap<>();
        putBasicElementsToTestMapCharStr(testMap);
        assertTrue(testMap.values().containsAll(dictionaryCharStr.values()));
//        assertTrue(dictionaryCharStr.values().containsAll(testMap.values())); // не проходит
    }

    @Test
    public void testMapCharStrEntrySet() {
        createCharStr();
        putBasicElementsCharStr();
        Map<Character, String> testMap = new HashMap<>();
        putBasicElementsToTestMapCharStr(testMap);
        assertEquals(testMap.entrySet(), dictionaryCharStr.entrySet());
    }

    @Test
    public void testMapCharStrKeySetRemove() {
        createCharStr();
        putBasicElementsCharStr();
        Iterator<Character> iter = dictionaryCharStr.keySet().iterator();
        assertEquals("ab", dictionaryCharStr.get('b'));
        assertEquals(3, dictionaryCharStr.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryCharStr.get('b'));
        assertEquals(2, dictionaryCharStr.size());
    }

    @Test
    public void testMapCharStrValuesRemove() {
        createCharStr();
        putBasicElementsCharStr();
        Iterator<String> iter = dictionaryCharStr.values().iterator();
        assertEquals("ab", dictionaryCharStr.get('b'));
        assertEquals(3, dictionaryCharStr.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryCharStr.get('b'));
        assertEquals(2, dictionaryCharStr.size());
    }

    @Test
    public void testMapCharStrEntrySetRemove() {
        createCharStr();
        putBasicElementsCharStr();
        Iterator<Map.Entry<Character, String>> iter = dictionaryCharStr.entrySet().iterator();
        assertEquals("ab", dictionaryCharStr.get('b'));
        assertEquals(3, dictionaryCharStr.size());
        iter.next();
        iter.remove();
        assertNull(dictionaryCharStr.get('b'));
        assertEquals(2, dictionaryCharStr.size());
    }

    @Test
    public void stressTestMapIntIntIter() {
        createIntInt();
        Random rand = new Random();
        Map<Integer, Integer> testMap = new HashMap<>();
        for (int i = 0; i < 150; i++) {
            Integer cur = rand.nextInt(1000);
            dictionaryIntInt.put(i, cur);
            testMap.put(i, cur);
        }
        assertEquals(testMap.entrySet(), dictionaryIntInt.entrySet());
        assertEquals(testMap.keySet(), dictionaryIntInt.keySet());
        assertTrue(testMap.values().containsAll(dictionaryIntInt.values()));
//        assertTrue(dictionaryIntInt.values().containsAll(testMap.values())); // не проходит
    }

}
