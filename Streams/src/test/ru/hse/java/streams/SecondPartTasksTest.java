package ru.hse.java.streams;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.hse.java.streams.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> expected = Arrays.asList("Hello, world!", "heheHello world", "!Hello!", "Hello!", "world Hello");
        List<String> actual = findQuotes(Arrays.asList("src/test/resources/file1.txt", "src/test/resources/file2.txt",
                "src/test/resources/file3.txt", "src/test/resources/file4.txt"), "Hello");
        Multiset<String> expectedSet = HashMultiset.create(expected);
        Multiset<String> actualSet = HashMultiset.create(actual);
        assertEquals(expectedSet, actualSet);
        Class<?>[] paramTypes = new Class[] {};
    }

    @Test
    public void testFindQuotesNonExistentSequence() {
        List<String> expected = Collections.emptyList();
        List<String> actual = findQuotes(Arrays.asList("src/test/resources/file1.txt", "src/test/resources/file2.txt",
                "src/test/resources/file3.txt", "src/test/resources/file4.txt"), "C++");
        assertEquals(expected, actual);
    }

    @Test
    public void testFindQuotesException() {
        assertThrows(RuntimeException.class, () -> findQuotes(Collections.singletonList("non-existent"), "Hello"));
        assertThrows(RuntimeException.class, () -> findQuotes(Arrays.asList("src/test/resources/file1.txt", "non-existent"), "Hello"));
    }

    @Test
    public void testPiDividedBy4() {
        for (int i = 0; i < 5; i++) {
            assertEquals(Math.PI / 4, piDividedBy4(), 1e-3);
        }
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> in = new HashMap<>();
        in.put("Author 1", Arrays.asList("comp 1", "comp 2"));
        in.put("Author 2", Collections.singletonList("comp 1"));
        in.put("Author 3", Arrays.asList("comp 1", "comp 2"));
        in.put("Author 4", Arrays.asList("comp 1", "comp 2", "comp 3", "comp 4", "comp 5"));
        in.put("Author 5", Arrays.asList("comp 1", "comp 2", "comp 3"));
        assertEquals("Author 4", findPrinter(in));
    }

    @Test
    public void testFindPrinterNoCompositions() {
        Map<String, List<String>> in = new HashMap<>();
        in.put("Author 1", Collections.emptyList());
        assertEquals("Author 1", findPrinter(in));
        in.put("Author 2", Collections.singletonList("comp 1"));
        in.put("Author 3", Arrays.asList("comp 1", "comp 2"));
        assertEquals("Author 3", findPrinter(in));
    }

    @Test
    public void testFindPrinterEmptyMap() {
        Map<String, List<String>> in = new HashMap<>();
        assertNull(findPrinter(in));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> first = new HashMap<>();
        first.put("prod1", 1);
        first.put("prod2", 2);
        first.put("prod3", 3);

        Map<String, Integer> second = new HashMap<>();
        second.put("prod1", 2);
        second.put("prod2", 4);

        Map<String, Integer> third = new HashMap<>();
        third.put("prod1", 10);
        third.put("prod2", 12);
        third.put("prod3", 23);
        third.put("prod4", 11);
        third.put("prod5", 0);

        Map<String, Integer> fourth = new HashMap<>();

        Map<String, Integer> ans = new HashMap<>();
        ans.put("prod1", 13);
        ans.put("prod2", 18);
        ans.put("prod3", 26);
        ans.put("prod4", 11);
        ans.put("prod5", 0);

        assertEquals(ans, calculateGlobalOrder(Arrays.asList(first, second, third, fourth)));
    }

}