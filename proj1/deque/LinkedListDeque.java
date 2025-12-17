package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> extends AbstractDeque<T> {

    /**
     * Doubly linked node.
     */
    private static class Node<T> {
        /**
         * The item of node.
         */
        private final T item;

        /**
         * The prev node and next node.
         */
        private Node<T> prev, next;

        private Node(T item) {
            this.item = item;
        }
    }

    /**
     * The head and tail of the deque.
     */
    private Node<T> head, tail;

    /**
     * The number of items in the deque.
     */
    private int size;

    /**
     * The constructor of empty `LinkedListDeque`.
     */
    public LinkedListDeque() {
        head = tail = null;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        var node = new Node<>(item);
        if (isEmpty()) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        var node = new Node<>(item);
        if (isEmpty()) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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

        var item = head.item;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }
        size--;
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        var item = tail.item;
        tail = tail.prev;
        if (tail == null) {
            head = null;
        } else {
            tail.next = null;
        }
        size--;
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        var cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur.item;
    }

    public T getRecursive(int index) {
       var node = getNode(index);
       return node == null ? null : node.item;
    }

    /**
     * Get node in index, maybe return null if index is out of range.
     */
    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        if (index == 0) {
            return head;
        }

        if (index == size - 1) {
            return tail;
        }

        if (index < size / 2) {
            return Objects.requireNonNull(getNode(index - 1)).next;
        } else {
            return Objects.requireNonNull(getNode(index + 1)).prev;  // 直接访问
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    /**
     * Iterator for `LinkedListDeque`.
     */
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node<T> current;

        public LinkedListDequeIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            var item = current.item;
            current = current.next;
            return item;
        }
    }
}
