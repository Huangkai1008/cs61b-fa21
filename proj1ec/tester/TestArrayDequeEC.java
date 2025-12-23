package tester;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import static org.junit.Assert.*;

/**
 * @author huangkai
 */
public class TestArrayDequeEC {
    
    @Test
    public void randomizedTest() {
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        
        StringBuilder message = new StringBuilder();
        
        for (int i = 0; i < 1000; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            
            if (operationNumber == 0) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                student.addFirst(randVal);
                message.append("addFirst(").append(randVal).append(")\n");
                
            } else if (operationNumber == 1) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                student.addLast(randVal);
                message.append("addLast(").append(randVal).append(")\n");
                
            } else if (operationNumber == 2) {
                // removeFirst
                if (correct.size() > 0) {
                    message.append("removeFirst()\n");
                    Integer expected = correct.removeFirst();
                    Integer actual = student.removeFirst();
                    assertEquals(message.toString(), expected, actual);
                }
                
            } else if (operationNumber == 3) {
                // removeLast
                if (correct.size() > 0) {
                    message.append("removeLast()\n");
                    Integer expected = correct.removeLast();
                    Integer actual = student.removeLast();
                    assertEquals(message.toString(), expected, actual);
                }
            }
        }
    }
}