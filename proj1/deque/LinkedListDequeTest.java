package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        var lld1 = new LinkedListDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        var lld1 = new LinkedListDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        var lld1 = new LinkedListDeque<Integer>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {
        var lld1 = new LinkedListDeque<String>();
        var lld2 = new LinkedListDeque<Double>();
        var lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {
        var lld1 = new LinkedListDeque<Integer>();

        assertNull("Should return null when removeFirst is called on an empty Deque,",
                lld1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,",
                lld1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {
        var lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }
    }

    @Test
    /* Test get method with valid indices */
    public void getTest() {
        var lld1 = new LinkedListDeque<Integer>();
        lld1.addLast(0);
        lld1.addLast(1);
        lld1.addLast(2);
        lld1.addLast(3);
        lld1.addLast(4);

        assertEquals("Should get first element", Integer.valueOf(0), lld1.get(0));
        assertEquals("Should get middle element", Integer.valueOf(2), lld1.get(2));
        assertEquals("Should get last element", Integer.valueOf(4), lld1.get(4));
    }

    @Test
    /* Test get method with invalid indices */
    public void getInvalidIndexTest() {
        var lld1 = new LinkedListDeque<String>();
        lld1.addLast("first");
        lld1.addLast("second");

        assertNull("Should return null for negative index", lld1.get(-1));
        assertNull("Should return null for index >= size", lld1.get(2));
        assertNull("Should return null for large index", lld1.get(100));
    }

    @Test
    /* Test getRecursive method */
    public void getRecursiveTest() {
        var lld1 = new LinkedListDeque<String>();
        lld1.addLast("a");
        lld1.addLast("b");
        lld1.addLast("c");
        lld1.addLast("d");
        lld1.addLast("e");

        assertEquals("Should get first element recursively", "a", lld1.getRecursive(0));
        assertEquals("Should get middle element recursively", "c", lld1.getRecursive(2));
        assertEquals("Should get last element recursively", "e", lld1.getRecursive(4));
        assertNull("Should return null for invalid index", lld1.getRecursive(-1));
        assertNull("Should return null for index >= size", lld1.getRecursive(5));
    }

    @Test
    /* Test alternating addFirst and addLast */
    public void alternatingAddTest() {
        var lld1 = new LinkedListDeque<Integer>();
        lld1.addFirst(1);
        lld1.addLast(2);
        lld1.addFirst(0);
        lld1.addLast(3);
        lld1.addFirst(-1);

        assertEquals("Size should be 5", 5, lld1.size());
        assertEquals("First element should be -1", Integer.valueOf(-1), lld1.get(0));
        assertEquals("Second element should be 0", Integer.valueOf(0), lld1.get(1));
        assertEquals("Third element should be 1", Integer.valueOf(1), lld1.get(2));
        assertEquals("Fourth element should be 2", Integer.valueOf(2), lld1.get(3));
        assertEquals("Fifth element should be 3", Integer.valueOf(3), lld1.get(4));
    }

    @Test
    /* Test alternating removeFirst and removeLast */
    public void alternatingRemoveTest() {
        var lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i);
        }

        assertEquals("Remove first should return 0", Integer.valueOf(0), lld1.removeFirst());
        assertEquals("Remove last should return 9", Integer.valueOf(9), lld1.removeLast());
        assertEquals("Remove first should return 1", Integer.valueOf(1), lld1.removeFirst());
        assertEquals("Remove last should return 8", Integer.valueOf(8), lld1.removeLast());

        assertEquals("Size should be 6", 6, lld1.size());
        assertEquals("First element should be 2", Integer.valueOf(2), lld1.get(0));
        assertEquals("Last element should be 7", Integer.valueOf(7), lld1.get(5));
    }

    @Test
    /* Test removing until empty and then adding again */
    public void removeAllThenAddTest() {
        var lld1 = new LinkedListDeque<String>();
        lld1.addLast("first");
        lld1.addLast("second");
        lld1.addLast("third");

        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();

        assertTrue("Should be empty after removing all", lld1.isEmpty());
        assertEquals("Size should be 0", 0, lld1.size());

        lld1.addFirst("new first");
        lld1.addLast("new last");

        assertEquals("Size should be 2", 2, lld1.size());
        assertEquals("First element should be 'new first'", "new first", lld1.get(0));
        assertEquals("Last element should be 'new last'", "new last", lld1.get(1));
    }

    @Test
    /* Test iterator functionality */
    public void iteratorTest() {
        var lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 5; i++) {
            lld1.addLast(i);
        }

        int expected = 0;
        for (Integer item : lld1) {
            assertEquals("Iterator should return elements in order", Integer.valueOf(expected), item);
            expected++;
        }
        assertEquals("Should iterate through all elements", 5, expected);
    }

    @Test
    /* Test iterator on empty deque */
    public void emptyIteratorTest() {
        var lld1 = new LinkedListDeque<String>();
        int count = 0;
        for (String item : lld1) {
            count++;
        }
        assertEquals("Iterator should not iterate on empty deque", 0, count);
    }

    @Test
    /* Test single element operations */
    public void singleElementTest() {
        var lld1 = new LinkedListDeque<Double>();
        lld1.addFirst(3.14);

        assertEquals("Size should be 1", 1, lld1.size());
        assertFalse("Should not be empty", lld1.isEmpty());
        assertEquals("Get should return the element", Double.valueOf(3.14), lld1.get(0));

        Double removed = lld1.removeLast();
        assertEquals("Removed element should be 3.14", Double.valueOf(3.14), removed);
        assertTrue("Should be empty after removal", lld1.isEmpty());
    }

    @Test
    /* Test adding and removing from both ends */
    public void bothEndsTest() {
        var lld1 = new LinkedListDeque<Character>();
        lld1.addFirst('b');
        lld1.addFirst('a');
        lld1.addLast('c');
        lld1.addLast('d');

        assertEquals("Should be 'a'", Character.valueOf('a'), lld1.removeFirst());
        assertEquals("Should be 'd'", Character.valueOf('d'), lld1.removeLast());
        assertEquals("Size should be 2", 2, lld1.size());
        assertEquals("First should be 'b'", Character.valueOf('b'), lld1.get(0));
        assertEquals("Last should be 'c'", Character.valueOf('c'), lld1.get(1));
    }

    @Test
    /* Test get on all positions after multiple operations */
    public void getAllPositionsTest() {
        var lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i * 10);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals("Get at position " + i + " should return " + (i * 10),
                    Integer.valueOf(i * 10), lld1.get(i));
        }
    }

    @Test
    /* Test that get doesn't modify the deque */
    public void getDoesntModifyTest() {
        var lld1 = new LinkedListDeque<String>();
        lld1.addLast("one");
        lld1.addLast("two");
        lld1.addLast("three");

        int originalSize = lld1.size();
        lld1.get(0);
        lld1.get(1);
        lld1.get(2);
        lld1.getRecursive(0);
        lld1.getRecursive(1);

        assertEquals("Size should not change after get operations", originalSize, lld1.size());
        assertEquals("Elements should remain unchanged", "one", lld1.get(0));
    }

    @Test
    /* Test with null elements (if allowed) */
    public void nullElementTest() {
        var lld1 = new LinkedListDeque<String>();
        lld1.addFirst("not null");
        lld1.addLast(null);
        lld1.addFirst(null);

        assertEquals("Size should be 3", 3, lld1.size());
        assertNull("First element should be null", lld1.get(0));
        assertEquals("Middle element should be 'not null'", "not null", lld1.get(1));
        assertNull("Last element should be null", lld1.get(2));
    }

    @Test
    /* Test stress test with mixed operations */
    public void stressTest() {
        var lld1 = new LinkedListDeque<Integer>();
        int N = 10000;

        // Add elements
        for (int i = 0; i < N; i++) {
            if (i % 2 == 0) {
                lld1.addFirst(i);
            } else {
                lld1.addLast(i);
            }
        }

        assertEquals("Size should be " + N, N, lld1.size());

        // Remove half
        for (int i = 0; i < N / 2; i++) {
            if (i % 2 == 0) {
                lld1.removeFirst();
            } else {
                lld1.removeLast();
            }
        }

        assertEquals("Size should be " + N / 2, N / 2, lld1.size());
        assertFalse("Should not be empty", lld1.isEmpty());
    }
}
