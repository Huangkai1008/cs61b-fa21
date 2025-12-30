package bstmap;

import java.util.Set;
import java.util.HashSet;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.*;
import org.junit.Test;

/** Tests of optional parts of lab 7. */
public class TestBSTMapExtra {

    /*
    * Sanity test for keySet, only here because it's optional
    */
    @Test
    public void sanityKeySetTest() {
    	BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        HashSet<String> values = new HashSet<String>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1);
            values.add("hi" + i);
        }
        assertEquals(455, b.size()); //keys are there
        Set<String> keySet = b.keySet();
        assertTrue(values.containsAll(keySet));
        assertTrue(keySet.containsAll(values));
    }

    /* Remove Test
     *
     * Note for testRemoveRoot:
     *
     * Just checking that c is gone (perhaps incorrectly)
     * assumes that remove is BST-structure preserving.
     *
     * More exhaustive tests could be done to verify
     * implementation of remove, but that would require doing
     * things like checking for inorder vs. preorder swaps,
     * and is unnecessary in this simple BST implementation.
     */
    @Test
    public void testRemoveRoot() {
        BSTMap<String,String> q = new BSTMap<String,String>();
        q.put("c","a");
        q.put("b","a");
        q.put("a","a");
        q.put("d","a");
        q.put("e","a"); // a b c d e
        assertTrue(null != q.remove("c"));
        assertFalse(q.containsKey("c"));
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("d"));
        assertTrue(q.containsKey("e"));
    }

    /* Remove Test 2
     * test the 3 different cases of remove
     */
    @Test
    public void testRemoveThreeCases() {
        BSTMap<String,String> q = new BSTMap<String,String>();
        q.put("c","a");
        q.put("b","a");
        q.put("a","a");
        q.put("d","a");
        q.put("e","a");                         // a b c d e
        assertTrue(null != q.remove("e"));      // a b c d
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("c"));
        assertTrue(q.containsKey("d"));
        assertTrue(null != q.remove("c"));      // a b d
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("d"));
        q.put("f","a");                         // a b d f
        assertTrue(null != q.remove("d"));      // a b f
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("f"));
    }

    /* Remove Test 3
    *  Checks that remove works correctly on root nodes
    *  when the node has only 1 or 0 children on either side. */
    @Test
    public void testRemoveRootEdge() {
        BSTMap rightChild = new BSTMap();
        rightChild.put('A', 1);
        rightChild.put('B', 2);
        Integer result = (Integer) rightChild.remove('A');
        assertTrue(result.equals(new Integer(1)));
        for (int i = 0; i < 10; i++) {
            rightChild.put((char) ('C'+i), 3+i);
        }
        rightChild.put('A', 100);
        assertTrue(((Integer) rightChild.remove('D')).equals(new Integer(4)));
        assertTrue(((Integer) rightChild.remove('G')).equals(new Integer(7)));
        assertTrue(((Integer) rightChild.remove('A')).equals(new Integer(100)));
        assertTrue(rightChild.size()==9);

        BSTMap leftChild = new BSTMap();
        leftChild.put('B', 1);
        leftChild.put('A', 2);
        assertTrue(((Integer) leftChild.remove('B')).equals(1));
        assertEquals(1, leftChild.size());
        assertEquals(null, leftChild.get('B'));

        BSTMap noChild = new BSTMap();
        noChild.put('Z', 15);
        assertTrue(((Integer) noChild.remove('Z')).equals(15));
        assertEquals(0, noChild.size());
        assertEquals(null, noChild.get('Z'));
    }

    /* Test for printInOrder method
     * Verifies that printInOrder prints nodes in sorted order
     */
    @Test
    public void testPrintInOrder() {
        BSTMap<Integer, String> b = new BSTMap<>();
        
        // Test empty tree
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        b.printInOrder();
        assertEquals("", outContent.toString());
        
        // Test single node
        b.put(5, "five");
        outContent.reset();
        b.printInOrder();
        assertTrue(outContent.toString().contains("key=5"));
        assertTrue(outContent.toString().contains("value=five"));
        
        // Test multiple nodes - should print in sorted order
        b.put(3, "three");
        b.put(7, "seven");
        b.put(1, "one");
        b.put(9, "nine");
        outContent.reset();
        b.printInOrder();
        
        String output = outContent.toString();
        // Verify all nodes are printed
        assertTrue(output.contains("key=1"));
        assertTrue(output.contains("key=3"));
        assertTrue(output.contains("key=5"));
        assertTrue(output.contains("key=7"));
        assertTrue(output.contains("key=9"));
        
        // Verify they appear in sorted order
        int pos1 = output.indexOf("key=1");
        int pos3 = output.indexOf("key=3");
        int pos5 = output.indexOf("key=5");
        int pos7 = output.indexOf("key=7");
        int pos9 = output.indexOf("key=9");
        
        assertTrue(pos1 < pos3);
        assertTrue(pos3 < pos5);
        assertTrue(pos5 < pos7);
        assertTrue(pos7 < pos9);
        
        // Restore original System.out
        System.setOut(originalOut);
    }

    /* Test for printInOrder with String keys
     * Verifies that printInOrder works with different key types
     */
    @Test
    public void testPrintInOrderWithStrings() {
        BSTMap<String, Integer> b = new BSTMap<>();
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        // Add nodes in non-sorted order
        b.put("dog", 1);
        b.put("cat", 2);
        b.put("elephant", 3);
        b.put("ant", 4);
        b.put("zebra", 5);
        
        b.printInOrder();
        
        String output = outContent.toString();
        
        // Verify all nodes are printed
        assertTrue(output.contains("key=ant"));
        assertTrue(output.contains("key=cat"));
        assertTrue(output.contains("key=dog"));
        assertTrue(output.contains("key=elephant"));
        assertTrue(output.contains("key=zebra"));
        
        // Verify they appear in alphabetical order
        int posAnt = output.indexOf("key=ant");
        int posCat = output.indexOf("key=cat");
        int posDog = output.indexOf("key=dog");
        int posElephant = output.indexOf("key=elephant");
        int posZebra = output.indexOf("key=zebra");
        
        assertTrue(posAnt < posCat);
        assertTrue(posCat < posDog);
        assertTrue(posDog < posElephant);
        assertTrue(posElephant < posZebra);
        
        // Restore original System.out
        System.setOut(originalOut);
    }

}
