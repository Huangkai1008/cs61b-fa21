package gh2;

import deque.Deque;
import deque.LinkedListDeque;

public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        // Create a buffer with capacity = SR / frequency. You'll need to
        // cast the result of this division operation into an int. For
        // better accuracy, use the Math.round() function before casting.
        // Your buffer should initially be filled with zeros.
        this(frequency, new LinkedListDeque<>());
    }

    // Private constructor for internal use and testing
    private GuitarString(double frequency, Deque<Double> deque) {
        buffer = deque;
        int capacity = Math.toIntExact(Math.round(SR / frequency));
        for (int i = 0; i < capacity; i++) {
            buffer.addLast(0.0);
        }
    }

    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        // Dequeue everything in buffer and replace with random numbers
        // between -0.5 and 0.5. Generate each number using:
        // double r = Math.random() - 0.5;
        //
        // Make sure to call Math.random() - 0.5 repeatedly for each
        // buffer position to generate different random numbers.
        for (int i = 0; i < buffer.size(); i++) {
            buffer.removeFirst();
            double item = Math.random() - 0.5;
            buffer.addLast(item);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        // Dequeue the front sample and enqueue a new sample that is
        // the average of the two samples multiplied by the DECAY factor.
        // Do not call StdAudio.play().
        double sample = buffer.removeFirst();
        double nextItem = buffer.get(0);
        double newItem = DECAY * 0.5 * (sample + nextItem);
        buffer.addLast(newItem);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        // Return the correct thing.
        return buffer.get(0);
    }
}
