package tester;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.*;

/**
 * @author huangkai
 */
public class TestArrayDequeEC {
    private static final int QTY = 100;
    private ArrayDequeSolution<Integer> deque1;
    private StudentArrayDeque<Integer> deque2;

    @Before
    public void setUp() throws Exception {
        deque1 = new ArrayDequeSolution<>();
        deque2 = new StudentArrayDeque<>();
    }

    @After
    public void tearDown() throws Exception {
        deque1 = null;
        deque2 = null;
    }

    @Test
    public void addFirstTest() {
        for (int i = 0; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addFirst(number);
            deque2.addFirst(number);
            assertEquals(deque1.get(i), deque2.get(i));
        }
    }

    @Test
    public void addLastTest() {
        for (int i = 0; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addLast(number);
            deque2.addLast(number);
            assertEquals(deque1.get(i), deque2.get(i));
        }
    }

    @Test
    public void isEmptyTest() {
        assertTrue(deque1.isEmpty());
        assertTrue(deque2.isEmpty());
        for (int i = 0; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addFirst(number);
            deque2.addFirst(number);
        }
        assertFalse(deque1.isEmpty());
        assertFalse(deque2.isEmpty());
    }

    @Test
    public void sizeTest() {
        assertEquals(deque1.size(), deque2.size());
        for (int i = 0; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addFirst(number);
            deque2.addFirst(number);
        }
        assertEquals(deque1.size(), deque2.size());
    }

    @Test
    public void removeFirstTest() {
        for (int i = 1; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addFirst(number);
            deque2.addFirst(number);
        }
        for (int i = 1; i <= QTY; i++) {
            Integer expected = deque1.removeFirst();
            Integer actual = deque2.removeFirst();
            assertEquals("Oh no!\nThis is bad:\n   Random number " + " not equal to " + expected + "!", expected, actual);
            assertEquals(deque1.size(), deque2.size());
        }
        assertEquals(deque1.size(), deque2.size());
    }

    @Test
    public void removeLastTest() {
        for (int i = 1; i <= QTY; i++) {
            Integer number = StdRandom.uniform(QTY);
            deque1.addFirst(number);
            deque2.addFirst(number);
        }
        for (int i = 1; i <= 10; i++) {
            Integer expected = deque1.removeLast();
            Integer actual = deque2.removeLast();
            assertEquals("Oh no!\nThis is bad:\n   Random number " + " not equal to " + expected + "!", expected, actual);
            assertEquals(deque1.size(), deque2.size());
        }
        assertEquals(deque1.size(), deque2.size());
    }
}