package deque;

import java.util.Iterator;

public class ArrayDeque<T> extends AbstractDeque<T> {
    /**
     * The starting size of array should be 8.
     */
    private static final int DEFAULT_CAPACITY = 8;

    /**
     * The resize factor of array deque.
     */
    private static final int RESIZE_FACTOR = 2;

    /**
     * The shrink factor of array deque.
     */
    private static final int SHRINK_FACTOR = 4;

    /**
     * The front index of deque (points to the first element).
     */
    private int front;

    /**
     * The rear index of deque (points to the position after the last element).
     */
    private int rear;

    /**
     * The array to carry deque items.
     */
    private Object[] items;

    /**
     * The number of items in the deque.
     */
    private int size;

    public ArrayDeque() {
        front = rear = 0;
        items = new Object[DEFAULT_CAPACITY + 1];
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        if (isFull()) {
            resize(capacity() * RESIZE_FACTOR);
        }

        front = (front - 1 + items.length) % items.length;
        items[front] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (isFull()) {
            resize(capacity() * RESIZE_FACTOR);
        }

        items[rear] = item;
        rear = (rear + 1) % items.length;
        size++;
    }

    @Override
    public boolean isEmpty() {
        return front == rear;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (T item: this) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T item = (T) items[front];
        items[front] = null;  // Help GC
        front = (front + 1) % items.length;
        size--;

        if (needShrink()) {
            resize(capacity() / RESIZE_FACTOR);
        }

        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        rear = (rear - 1 + items.length) % items.length;
        @SuppressWarnings("unchecked")
        T item = (T) items[rear];
        items[rear] = null;  // Help GC
        size--;

        if (needShrink()) {
            resize(capacity() / RESIZE_FACTOR);
        }

        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T item = (T) items[(front + index) % items.length];
        return item;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    /**
     * Iterator for `ArrayDeque`
     */
    private class ArrayDequeIterator implements Iterator<T> {
        private int cur = 0;

        @Override
        public boolean hasNext() {
            return cur < size;
        }

        @Override
        public T next() {
            @SuppressWarnings("unchecked")
            T item = (T) items[(front + cur) % items.length];
            cur++;
            return item;
        }
    }

    private int capacity() {
        return items.length - 1;
    }

    private boolean isFull() {
        var length = items.length;
        return (rear + 1) % length == front;
    }

    private boolean needShrink() {
        return size == capacity() / SHRINK_FACTOR && capacity() / 2 != 0;
    }

    /**
     * Resize array deque with new capacity.
     */
    private void resize(int capacity) {
        var newItems = new Object[capacity + 1];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[(front + i) % items.length];
        }
        items = newItems;
        front = 0;
        rear = size;
    }
}
