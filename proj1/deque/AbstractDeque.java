package deque;

import java.util.Objects;

public abstract class AbstractDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * Returns whether or not the parameter o is equal to the Deque.
     * o is considered equal if it is a Deque and if it contains the same contents
     * (as goverened by the generic T’s equals method) in the same order.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Deque)) {
            return false;
        }

        var that = (Deque<?>) obj;

        if (this.size() != that.size()) {
            return false;
        }

        var thisIter = this.iterator();
        var thatIter = that.iterator();
        while (thisIter.hasNext()) {
            if (!Objects.equals(thisIter.next(), thatIter.next())) {
                return false;
            }
        }
        return true;
    }
}
