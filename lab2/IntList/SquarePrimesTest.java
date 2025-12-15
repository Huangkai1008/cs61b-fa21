package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    /** 测试：没有质数的情况 */
    @Test
    public void testNoPrimes() {
        IntList lst = IntList.of(4, 6, 8, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 8 -> 10", lst.toString());
        assertFalse(changed);
    }

    /** 测试：全是质数的情况 */
    @Test
    public void testAllPrimes() {
        IntList lst = IntList.of(2, 3, 5, 7);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 9 -> 25 -> 49", lst.toString());
        assertTrue(changed);
    }

    /** 测试：连续两个质数 */
    @Test
    public void testConsecutivePrimes() {
        IntList lst = IntList.of(2, 3, 4, 5);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 9 -> 4 -> 25", lst.toString());
        assertTrue(changed);
    }

    /** 测试：单个质数 */
    @Test
    public void testSinglePrime() {
        IntList lst = IntList.of(7);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("49", lst.toString());
        assertTrue(changed);
    }

    /** 测试：单个非质数 */
    @Test
    public void testSingleNonPrime() {
        IntList lst = IntList.of(10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("10", lst.toString());
        assertFalse(changed);
    }

    /** 测试：质数在开头 */
    @Test
    public void testPrimeAtStart() {
        IntList lst = IntList.of(2, 4, 6, 8);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 4 -> 6 -> 8", lst.toString());
        assertTrue(changed);
    }

    /** 测试：质数在末尾 */
    @Test
    public void testPrimeAtEnd() {
        IntList lst = IntList.of(4, 6, 8, 11);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 8 -> 121", lst.toString());
        assertTrue(changed);
    }

    /** 测试：包含 1（1 不是质数） */
    @Test
    public void testWithOne() {
        IntList lst = IntList.of(1, 2, 1, 3);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("1 -> 4 -> 1 -> 9", lst.toString());
        assertTrue(changed);
    }
}
