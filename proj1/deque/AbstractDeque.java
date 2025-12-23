package deque;

import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T's equals method) in the same order.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Deque)) {
            return false;
        }

        Deque<?> that = (Deque<?>) obj;

        if (this.size() != that.size()) {
            return false;
        }

        // Check if that is also Iterable
        if (!(obj instanceof Iterable)) {
            return false;
        }

        Iterator<?> thatIter = ((Iterable<?>) obj).iterator();

        for (T t : this) {
            if (!Objects.equals(t, thatIter.next())) {
                return false;
            }
        }
        return true;
    }
}
