package deque;

import org.junit.Test;
import java.util.Comparator;
import edu.princeton.cs.algs4.Stopwatch;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    /**
     * Comparator for integers in natural order
     */
    private static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    /**
     * Comparator for integers in reverse order
     */
    private static class ReverseIntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    }

    /**
     * Comparator for integers by absolute value
     */
    private static class AbsoluteValueComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return Math.abs(a) - Math.abs(b);
        }
    }

    /**
     * Comparator for strings by length
     */
    private static class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    /**
     * Comparator for strings in alphabetical order
     */
    private static class StringAlphabeticalComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    /**
     * Comparator for strings in reverse alphabetical order
     */
    private static class ReverseStringComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }

    @Test
    public void testMaxWithDefaultComparator() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);
        mad.addLast(8);

        assertEquals("Max should be 10", Integer.valueOf(10), mad.max());
    }

    @Test
    public void testMaxWithDifferentComparator() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);
        mad.addLast(8);

        // Using default comparator (natural order)
        assertEquals("Max with default comparator should be 10", 
                     Integer.valueOf(10), mad.max());

        // Using reverse comparator
        assertEquals("Max with reverse comparator should be 3", 
                     Integer.valueOf(3), mad.max(new ReverseIntComparator()));
    }

    @Test
    public void testMaxEmptyDeque() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        assertNull("Max of empty deque should be null", mad.max());
        assertNull("Max of empty deque with different comparator should be null", 
                   mad.max(new ReverseIntComparator()));
    }

    @Test
    public void testMaxSingleElement() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(42);

        assertEquals("Max of single element should be that element", 
                     Integer.valueOf(42), mad.max());
    }

    @Test
    public void testMaxAllSameElements() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(7);
        mad.addLast(7);
        mad.addLast(7);
        mad.addLast(7);

        assertEquals("Max of all same elements should be 7", 
                     Integer.valueOf(7), mad.max());
    }

    @Test
    public void testMaxWithNegativeNumbers() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(-5);
        mad.addLast(-10);
        mad.addLast(-3);
        mad.addLast(-8);

        assertEquals("Max should be -3", Integer.valueOf(-3), mad.max());
    }

    @Test
    public void testMaxWithMixedNumbers() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(-5);
        mad.addLast(10);
        mad.addLast(-3);
        mad.addLast(8);
        mad.addLast(0);

        assertEquals("Max should be 10", Integer.valueOf(10), mad.max());
    }

    @Test
    public void testMaxWithAbsoluteValueComparator() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(-5);
        mad.addLast(3);
        mad.addLast(-10);
        mad.addLast(7);

        // Natural order: max is 7
        assertEquals("Max with natural order should be 7", 
                     Integer.valueOf(7), mad.max());

        // Absolute value: max is -10 (abs = 10)
        assertEquals("Max with absolute value should be -10", 
                     Integer.valueOf(-10), mad.max(new AbsoluteValueComparator()));
    }

    @Test
    public void testMaxWithStrings() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringLengthComparator());
        mad.addLast("cat");
        mad.addLast("elephant");
        mad.addLast("dog");
        mad.addLast("bird");

        assertEquals("Max by length should be 'elephant'", "elephant", mad.max());

        // Test with alphabetical comparator
        assertEquals("Max alphabetically should be 'elephant'", 
                     "elephant", mad.max(new StringAlphabeticalComparator()));

        // Test with reverse alphabetical comparator (bird < cat < dog < elephant in reverse)
        assertEquals("Max reverse alphabetically should be 'bird'", 
                     "bird", mad.max(new ReverseStringComparator()));
    }

    @Test
    public void testMaxAfterRemoval() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);

        assertEquals("Max should be 10", Integer.valueOf(10), mad.max());

        mad.removeLast();  // Remove 3
        assertEquals("Max should still be 10", Integer.valueOf(10), mad.max());

        mad.removeLast();  // Remove 10
        assertEquals("Max should now be 5", Integer.valueOf(5), mad.max());

        mad.removeLast();  // Remove 5
        assertNull("Max of empty deque should be null", mad.max());
    }

    @Test
    public void testMaxWithAddFirst() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addFirst(5);
        mad.addFirst(10);
        mad.addFirst(3);
        mad.addFirst(8);

        assertEquals("Max should be 10", Integer.valueOf(10), mad.max());
    }

    @Test
    public void testMaxWithMixedOperations() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addFirst(5);
        mad.addLast(10);
        mad.addFirst(3);
        mad.addLast(8);
        mad.addFirst(15);

        assertEquals("Max should be 15", Integer.valueOf(15), mad.max());

        mad.removeFirst();  // Remove 15
        assertEquals("Max should now be 10", Integer.valueOf(10), mad.max());
    }

    @Test
    public void testMaxLargeDeque() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        for (int i = 0; i < 1000; i++) {
            mad.addLast(i);
        }

        assertEquals("Max should be 999", Integer.valueOf(999), mad.max());
    }

    @Test
    public void testMaxWithDuplicateMaxValues() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(10);
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);
        mad.addLast(10);

        // Any of the 10s is acceptable
        assertEquals("Max should be 10", Integer.valueOf(10), mad.max());
    }

    @Test
    public void testMaxAtBeginning() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(100);
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);

        assertEquals("Max should be 100", Integer.valueOf(100), mad.max());
    }

    @Test
    public void testMaxAtEnd() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);
        mad.addLast(100);

        assertEquals("Max should be 100", Integer.valueOf(100), mad.max());
    }

    @Test
    public void testMaxInMiddle() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(100);
        mad.addLast(3);

        assertEquals("Max should be 100", Integer.valueOf(100), mad.max());
    }

    @Test
    public void testMaxAfterRemoveFirst() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(10);
        mad.addLast(5);
        mad.addLast(8);

        mad.removeFirst();  // Remove 10
        assertEquals("Max should be 8 after removing first", Integer.valueOf(8), mad.max());
    }

    @Test
    public void testMaxWithMultipleComparators() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(-5);
        mad.addLast(3);
        mad.addLast(-10);
        mad.addLast(7);

        // Test with three different comparators
        assertEquals("Natural order max", Integer.valueOf(7), mad.max());
        assertEquals("Reverse order max", Integer.valueOf(-10), 
                     mad.max(new ReverseIntComparator()));
        assertEquals("Absolute value max", Integer.valueOf(-10), 
                     mad.max(new AbsoluteValueComparator()));
    }

    @Test
    public void testMaxPreservesDequeContents() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);

        int originalSize = mad.size();
        mad.max();
        mad.max(new ReverseIntComparator());

        assertEquals("Size should not change after max operations", originalSize, mad.size());
        assertEquals("First element should still be 5", Integer.valueOf(5), mad.get(0));
        assertEquals("Second element should still be 10", Integer.valueOf(10), mad.get(1));
        assertEquals("Third element should still be 3", Integer.valueOf(3), mad.get(2));
    }

    @Test
    public void testMaxWithCharacters() {
        Comparator<Character> charComparator = Comparator.naturalOrder();
        MaxArrayDeque<Character> mad = new MaxArrayDeque<>(charComparator);
        mad.addLast('d');
        mad.addLast('a');
        mad.addLast('z');
        mad.addLast('m');

        assertEquals("Max character should be 'z'", Character.valueOf('z'), mad.max());
    }

    @Test
    public void testMaxWithDoubles() {
        Comparator<Double> doubleComparator = Comparator.naturalOrder();
        MaxArrayDeque<Double> mad = new MaxArrayDeque<>(doubleComparator);
        mad.addLast(3.14);
        mad.addLast(2.71);
        mad.addLast(9.99);
        mad.addLast(1.41);

        assertEquals("Max double should be 9.99", Double.valueOf(9.99), mad.max(), 0.001);
    }

    @Test
    public void testMaxAfterClear() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        mad.addLast(5);
        mad.addLast(10);
        mad.addLast(3);

        // Clear the deque
        while (!mad.isEmpty()) {
            mad.removeFirst();
        }

        assertNull("Max of cleared deque should be null", mad.max());
    }

    @Test
    public void testMaxWithRepeatedOperations() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        
        // Add and check max multiple times
        mad.addLast(5);
        assertEquals(Integer.valueOf(5), mad.max());
        
        mad.addLast(10);
        assertEquals(Integer.valueOf(10), mad.max());
        
        mad.addLast(3);
        assertEquals(Integer.valueOf(10), mad.max());
        
        mad.addLast(15);
        assertEquals(Integer.valueOf(15), mad.max());
        
        mad.removeFirst();  // Remove 5
        assertEquals(Integer.valueOf(15), mad.max());
        
        mad.removeLast();  // Remove 15
        assertEquals(Integer.valueOf(10), mad.max());
    }

    /* ==================== Performance Tests ==================== */

    @Test
    public void testMaxPerformanceSmall() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 1000;
        
        // Add N elements
        for (int i = 0; i < N; i++) {
            mad.addLast(i);
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            mad.max();
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Small scale (N=%d): %.4f seconds for 100 max() calls (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 1.0);
    }

    @Test
    public void testMaxPerformanceMedium() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 10000;
        
        // Add N elements
        for (int i = 0; i < N; i++) {
            mad.addLast(i);
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            mad.max();
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Medium scale (N=%d): %.4f seconds for 100 max() calls (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceLarge() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 100000;
        
        // Add N elements
        for (int i = 0; i < N; i++) {
            mad.addLast(i);
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            mad.max();
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Large scale (N=%d): %.4f seconds for 100 max() calls (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 5.0);
    }

    @Test
    public void testMaxPerformanceWithDifferentComparators() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 10000;
        
        // Add N elements
        for (int i = 0; i < N; i++) {
            mad.addLast(i);
        }
        
        // Measure max() with different comparators
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            mad.max();  // Default comparator
            mad.max(new ReverseIntComparator());
            mad.max(new AbsoluteValueComparator());
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Different comparators (N=%d): %.4f seconds for 300 max() calls (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 300);
        assertTrue("Should complete in reasonable time", timeInSeconds < 3.0);
    }

    @Test
    public void testMaxPerformanceWorstCase() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 10000;
        
        // Add elements in reverse order (max at the end)
        for (int i = N - 1; i >= 0; i--) {
            mad.addLast(i);
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            Integer max = mad.max();
            assertEquals("Max should be N-1", Integer.valueOf(N - 1), max);
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Worst case (max at end, N=%d): %.4f seconds (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceBestCase() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 10000;
        
        // Add elements with max at the beginning
        mad.addLast(N);
        for (int i = 0; i < N - 1; i++) {
            mad.addLast(i);
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            Integer max = mad.max();
            assertEquals("Max should be N", Integer.valueOf(N), max);
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Best case (max at start, N=%d): %.4f seconds (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceRandomData() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 10000;
        
        // Add random elements
        java.util.Random rand = new java.util.Random(42);  // Fixed seed for reproducibility
        for (int i = 0; i < N; i++) {
            mad.addLast(rand.nextInt(N * 10));
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        Integer max = null;
        for (int i = 0; i < 100; i++) {
            max = mad.max();
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("Random data (N=%d): %.4f seconds (%.2f μs/op), max=%d%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100, max);
        assertNotNull("Max should not be null", max);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceWithModifications() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
        int N = 5000;
        int maxCalls = 0;
        
        Stopwatch sw = new Stopwatch();
        
        // Interleave additions, removals, and max calls
        for (int i = 0; i < N; i++) {
            mad.addLast(i);
            if (i % 10 == 0) {
                mad.max();
                maxCalls++;
            }
        }
        
        for (int i = 0; i < N / 2; i++) {
            mad.removeFirst();
            if (i % 10 == 0) {
                mad.max();
                maxCalls++;
            }
        }
        
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("With modifications (N=%d): %.4f seconds (%d max calls, %.2f μs/op)%n", 
                         N, timeInSeconds, maxCalls, timeInSeconds * 1e6 / maxCalls);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceStrings() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringLengthComparator());
        int N = 5000;
        
        // Add strings of varying lengths
        for (int i = 0; i < N; i++) {
            mad.addLast("a".repeat(i % 100));
        }
        
        // Measure max() performance
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            mad.max();
        }
        double timeInSeconds = sw.elapsedTime();
        
        System.out.printf("String data (N=%d): %.4f seconds (%.2f μs/op)%n", 
                         N, timeInSeconds, timeInSeconds * 1e6 / 100);
        assertTrue("Should complete in reasonable time", timeInSeconds < 2.0);
    }

    @Test
    public void testMaxPerformanceComparison() {
        int N = 10000;
        int numCalls = 50;
        
        // Test 1: Integer comparisons
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<>(new IntComparator());
        for (int i = 0; i < N; i++) {
            mad1.addLast(i);
        }
        
        Stopwatch sw1 = new Stopwatch();
        for (int i = 0; i < numCalls; i++) {
            mad1.max();
        }
        double time1 = sw1.elapsedTime();
        
        // Test 2: String comparisons
        MaxArrayDeque<String> mad2 = new MaxArrayDeque<>(new StringAlphabeticalComparator());
        for (int i = 0; i < N; i++) {
            mad2.addLast("string" + i);
        }
        
        Stopwatch sw2 = new Stopwatch();
        for (int i = 0; i < numCalls; i++) {
            mad2.max();
        }
        double time2 = sw2.elapsedTime();
        
        System.out.printf("Performance comparison (N=%d, %d calls):%n", N, numCalls);
        System.out.printf("  Integer comparison: %.4f seconds (%.2f μs/op)%n", 
                         time1, time1 * 1e6 / numCalls);
        System.out.printf("  String comparison:  %.4f seconds (%.2f μs/op)%n", 
                         time2, time2 * 1e6 / numCalls);
        System.out.printf("  Ratio (String/Integer): %.2fx%n", time2 / time1);
        
        assertTrue("Integer comparison should complete quickly", time1 < 1.0);
        assertTrue("String comparison should complete in reasonable time", time2 < 2.0);
    }

    @Test
    public void testMaxScalability() {
        System.out.println("\n=== Scalability Test ===");
        System.out.printf("%12s %12s %12s %12s %12s%n", 
                         "N", "time (s)", "# ops", "microsec/op", "ratio");
        System.out.println("--------------------------------------------------------------------");
        
        int[] sizes = {100, 500, 1000, 5000, 10000, 50000, 100000};
        double prevTimePerOp = 0;
        
        for (int N : sizes) {
            MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntComparator());
            
            // Add elements
            for (int i = 0; i < N; i++) {
                mad.addLast(i);
            }
            
            // Measure max() performance with multiple calls for better accuracy
            // Use more operations for smaller N to get measurable times
            int numOps = N <= 1000 ? 1000 : (N <= 10000 ? 100 : 10);
            
            Stopwatch sw = new Stopwatch();
            Integer max = null;
            for (int i = 0; i < numOps; i++) {
                max = mad.max();
            }
            double timeInSeconds = sw.elapsedTime();
            double timePerOp = (timeInSeconds / numOps) * 1e6;  // Microseconds per operation
            
            String ratioStr = prevTimePerOp > 0 && prevTimePerOp > 0.01
                ? String.format("%.2f", timePerOp / prevTimePerOp) 
                : "-";
            
            System.out.printf("%12d %12.6f %12d %12.2f %12s%n", 
                             N, timeInSeconds, numOps, timePerOp, ratioStr);
            
            assertEquals("Max should be N-1", Integer.valueOf(N - 1), max);
            if (timePerOp > 0.01) {  // Only update if time is measurable
                prevTimePerOp = timePerOp;
            }
        }
        
        System.out.println("\nNote: Ratio shows time growth factor (should be ~linear with N)");
    }
}

