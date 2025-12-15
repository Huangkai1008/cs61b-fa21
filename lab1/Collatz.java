/** Class that prints the Collatz sequence starting from a given number.
 *  @author huang.kai
 */
public class Collatz {

    /**
     *
     * @param n The previous number.
     * @return The next number.
     */
    public static int nextNumber(int n) {
        if (n % 2 == 0) {
            n = n / 2;
        } else {
            n = n * 3 + 1;
        }
        return n;
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

