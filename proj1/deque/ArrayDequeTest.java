package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic array deque tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        var ad1 = new ArrayDeque<String>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    @Test
    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    public void addRemoveTest() {
        var ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        var ad1 = new ArrayDeque<Integer>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        var ad1 = new ArrayDeque<String>();
        var ad2 = new ArrayDeque<Double>();
        var ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        var ad1 = new ArrayDeque<Integer>();

        assertNull("Should return null when removeFirst is called on an empty Deque,",
                ad1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,",
                ad1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigArrayDequeTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }
    }

    @Test
    /* Test get method with valid indices */
    public void getTest() {
        var ad1 = new ArrayDeque<Integer>();
        ad1.addLast(0);
        ad1.addLast(1);
        ad1.addLast(2);
        ad1.addLast(3);
        ad1.addLast(4);

        assertEquals("Should get first element", Integer.valueOf(0), ad1.get(0));
        assertEquals("Should get middle element", Integer.valueOf(2), ad1.get(2));
        assertEquals("Should get last element", Integer.valueOf(4), ad1.get(4));
    }

    @Test
    /* Test get method with invalid indices */
    public void getInvalidIndexTest() {
        var ad1 = new ArrayDeque<String>();
        ad1.addLast("first");
        ad1.addLast("second");

        assertNull("Should return null for negative index", ad1.get(-1));
        assertNull("Should return null for index >= size", ad1.get(2));
        assertNull("Should return null for large index", ad1.get(100));
    }

    @Test
    /* Test alternating addFirst and addLast */
    public void alternatingAddTest() {
        var ad1 = new ArrayDeque<Integer>();
        ad1.addFirst(1);
        ad1.addLast(2);
        ad1.addFirst(0);
        ad1.addLast(3);
        ad1.addFirst(-1);

        assertEquals("Size should be 5", 5, ad1.size());
        assertEquals("First element should be -1", Integer.valueOf(-1), ad1.get(0));
        assertEquals("Second element should be 0", Integer.valueOf(0), ad1.get(1));
        assertEquals("Third element should be 1", Integer.valueOf(1), ad1.get(2));
        assertEquals("Fourth element should be 2", Integer.valueOf(2), ad1.get(3));
        assertEquals("Fifth element should be 3", Integer.valueOf(3), ad1.get(4));
    }

    @Test
    /* Test alternating removeFirst and removeLast */
    public void alternatingRemoveTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            ad1.addLast(i);
        }

        assertEquals("Remove first should return 0", Integer.valueOf(0), ad1.removeFirst());
        assertEquals("Remove last should return 9", Integer.valueOf(9), ad1.removeLast());
        assertEquals("Remove first should return 1", Integer.valueOf(1), ad1.removeFirst());
        assertEquals("Remove last should return 8", Integer.valueOf(8), ad1.removeLast());

        assertEquals("Size should be 6", 6, ad1.size());
        assertEquals("First element should be 2", Integer.valueOf(2), ad1.get(0));
        assertEquals("Last element should be 7", Integer.valueOf(7), ad1.get(5));
    }

    @Test
    /* Test removing until empty and then adding again */
    public void removeAllThenAddTest() {
        var ad1 = new ArrayDeque<String>();
        ad1.addLast("first");
        ad1.addLast("second");
        ad1.addLast("third");

        ad1.removeFirst();
        ad1.removeFirst();
        ad1.removeFirst();

        assertTrue("Should be empty after removing all", ad1.isEmpty());
        assertEquals("Size should be 0", 0, ad1.size());

        ad1.addFirst("new first");
        ad1.addLast("new last");

        assertEquals("Size should be 2", 2, ad1.size());
        assertEquals("First element should be 'new first'", "new first", ad1.get(0));
        assertEquals("Last element should be 'new last'", "new last", ad1.get(1));
    }

    @Test
    /* Test iterator functionality */
    public void iteratorTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 5; i++) {
            ad1.addLast(i);
        }

        int expected = 0;
        for (Integer item : ad1) {
            assertEquals("Iterator should return elements in order", Integer.valueOf(expected), item);
            expected++;
        }
        assertEquals("Should iterate through all elements", 5, expected);
    }

    @Test
    /* Test iterator on empty deque */
    public void emptyIteratorTest() {
        var ad1 = new ArrayDeque<String>();
        int count = 0;
        for (String item : ad1) {
            count++;
        }
        assertEquals("Iterator should not iterate on empty deque", 0, count);
    }

    @Test
    /* Test single element operations */
    public void singleElementTest() {
        var ad1 = new ArrayDeque<Double>();
        ad1.addFirst(3.14);

        assertEquals("Size should be 1", 1, ad1.size());
        assertFalse("Should not be empty", ad1.isEmpty());
        assertEquals("Get should return the element", Double.valueOf(3.14), ad1.get(0));

        Double removed = ad1.removeLast();
        assertEquals("Removed element should be 3.14", Double.valueOf(3.14), removed);
        assertTrue("Should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Test adding and removing from both ends */
    public void bothEndsTest() {
        var ad1 = new ArrayDeque<Character>();
        ad1.addFirst('b');
        ad1.addFirst('a');
        ad1.addLast('c');
        ad1.addLast('d');

        assertEquals("Should be 'a'", Character.valueOf('a'), ad1.removeFirst());
        assertEquals("Should be 'd'", Character.valueOf('d'), ad1.removeLast());
        assertEquals("Size should be 2", 2, ad1.size());
        assertEquals("First should be 'b'", Character.valueOf('b'), ad1.get(0));
        assertEquals("Last should be 'c'", Character.valueOf('c'), ad1.get(1));
    }

    @Test
    /* Test get on all positions after multiple operations */
    public void getAllPositionsTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            ad1.addLast(i * 10);
        }

        for (int i = 0; i < 10; i++) {
            assertEquals("Get at position " + i + " should return " + (i * 10),
                    Integer.valueOf(i * 10), ad1.get(i));
        }
    }

    @Test
    /* Test that get doesn't modify the deque */
    public void getDoesntModifyTest() {
        var ad1 = new ArrayDeque<String>();
        ad1.addLast("one");
        ad1.addLast("two");
        ad1.addLast("three");

        int originalSize = ad1.size();
        ad1.get(0);
        ad1.get(1);
        ad1.get(2);

        assertEquals("Size should not change after get operations", originalSize, ad1.size());
        assertEquals("Elements should remain unchanged", "one", ad1.get(0));
    }

    @Test
    /* Test with null elements (if allowed) */
    public void nullElementTest() {
        var ad1 = new ArrayDeque<String>();
        ad1.addFirst("not null");
        ad1.addLast(null);
        ad1.addFirst(null);

        assertEquals("Size should be 3", 3, ad1.size());
        assertNull("First element should be null", ad1.get(0));
        assertEquals("Middle element should be 'not null'", "not null", ad1.get(1));
        assertNull("Last element should be null", ad1.get(2));
    }

    @Test
    /* Test stress test with mixed operations */
    public void stressTest() {
        var ad1 = new ArrayDeque<Integer>();
        int N = 10000;

        // Add elements
        for (int i = 0; i < N; i++) {
            if (i % 2 == 0) {
                ad1.addFirst(i);
            } else {
                ad1.addLast(i);
            }
        }

        assertEquals("Size should be " + N, N, ad1.size());

        // Remove half
        for (int i = 0; i < N / 2; i++) {
            if (i % 2 == 0) {
                ad1.removeFirst();
            } else {
                ad1.removeLast();
            }
        }

        assertEquals("Size should be " + N / 2, N / 2, ad1.size());
        assertFalse("Should not be empty", ad1.isEmpty());
    }

    @Test
    /* Test array resizing by adding many elements */
    public void resizeTest() {
        var ad1 = new ArrayDeque<Integer>();
        // Add more than initial capacity (8)
        for (int i = 0; i < 100; i++) {
            ad1.addLast(i);
        }

        assertEquals("Size should be 100", 100, ad1.size());
        
        // Verify all elements are still accessible
        for (int i = 0; i < 100; i++) {
            assertEquals("Element at index " + i + " should be " + i,
                    Integer.valueOf(i), ad1.get(i));
        }
    }

    @Test
    /* Test array shrinking by removing many elements */
    public void shrinkTest() {
        var ad1 = new ArrayDeque<Integer>();
        // Add many elements
        for (int i = 0; i < 100; i++) {
            ad1.addLast(i);
        }

        // Remove most elements
        for (int i = 0; i < 90; i++) {
            ad1.removeFirst();
        }

        assertEquals("Size should be 10", 10, ad1.size());
        
        // Verify remaining elements
        for (int i = 0; i < 10; i++) {
            assertEquals("Element at index " + i + " should be " + (i + 90),
                    Integer.valueOf(i + 90), ad1.get(i));
        }
    }

    @Test
    /* Test circular array behavior */
    public void circularArrayTest() {
        var ad1 = new ArrayDeque<Integer>();
        
        // Add elements to trigger wrapping
        for (int i = 0; i < 5; i++) {
            ad1.addLast(i);
        }
        
        // Remove from front
        ad1.removeFirst();
        ad1.removeFirst();
        
        // Add more to front (should wrap around)
        ad1.addFirst(-1);
        ad1.addFirst(-2);
        
        assertEquals("Size should be 5", 5, ad1.size());
        assertEquals("First element should be -2", Integer.valueOf(-2), ad1.get(0));
        assertEquals("Second element should be -1", Integer.valueOf(-1), ad1.get(1));
        assertEquals("Third element should be 2", Integer.valueOf(2), ad1.get(2));
    }

    @Test
    /* Test adding to front repeatedly */
    public void addFirstRepeatedlyTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 20; i++) {
            ad1.addFirst(i);
        }

        assertEquals("Size should be 20", 20, ad1.size());
        
        // Elements should be in reverse order
        for (int i = 0; i < 20; i++) {
            assertEquals("Element at index " + i + " should be " + (19 - i),
                    Integer.valueOf(19 - i), ad1.get(i));
        }
    }

    @Test
    /* Test removing from last repeatedly */
    public void removeLastRepeatedlyTest() {
        var ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            ad1.addLast(i);
        }

        for (int i = 9; i >= 0; i--) {
            assertEquals("Should remove " + i, Integer.valueOf(i), ad1.removeLast());
        }

        assertTrue("Should be empty", ad1.isEmpty());
    }

    @Test
    /* Test interleaved operations */
    public void interleavedOperationsTest() {
        var ad1 = new ArrayDeque<Integer>();
        
        ad1.addLast(1);
        ad1.addFirst(0);
        ad1.addLast(2);
        assertEquals("Size should be 3", 3, ad1.size());
        
        assertEquals("Remove first should be 0", Integer.valueOf(0), ad1.removeFirst());
        ad1.addLast(3);
        assertEquals("Remove last should be 3", Integer.valueOf(3), ad1.removeLast());
        
        assertEquals("Size should be 2", 2, ad1.size());
        assertEquals("First element should be 1", Integer.valueOf(1), ad1.get(0));
        assertEquals("Last element should be 2", Integer.valueOf(2), ad1.get(1));
    }

    @Test
    /* Test that operations work correctly after multiple resize cycles */
    public void multipleResizeCyclesTest() {
        var ad1 = new ArrayDeque<Integer>();
        
        // Grow
        for (int i = 0; i < 50; i++) {
            ad1.addLast(i);
        }
        
        // Shrink
        for (int i = 0; i < 40; i++) {
            ad1.removeFirst();
        }
        
        // Grow again
        for (int i = 50; i < 100; i++) {
            ad1.addLast(i);
        }
        
        assertEquals("Size should be 60", 60, ad1.size());
        
        // Verify first few elements
        assertEquals("First element should be 40", Integer.valueOf(40), ad1.get(0));
        assertEquals("Second element should be 41", Integer.valueOf(41), ad1.get(1));
    }
}

