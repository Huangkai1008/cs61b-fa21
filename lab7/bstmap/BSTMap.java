package bstmap;


import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    /**
     * Binary Search Tree Node.
     */
    private static class BSTNode<K, V> {
        /**
         * The key of node.
         */
        @Nonnull
        private K key;

        /**
         * The value of node.
         */
        @Nonnull
        private V value;

        /**
         * The left child of the node.
         */
        @Nullable
        private BSTNode<K, V> left;

        /**
         * The right child of the node.
         */
        @Nullable
        private BSTNode<K, V> right;

        public BSTNode(@Nonnull K key, @Nonnull V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("BSTNode(key=%s, value=%s)", key, value);
        }
    }

    /**
     * The root node of the tree.
     */
    @Nullable
    private BSTNode<K, V> root;

    public BSTMap() {
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    @Override
    public V get(K key) {
        return get(root, key);
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (K key : this) {
            set.add(key);
        }
        return set;
    }

    @Override
    public V remove(K key) {
        V actualValue = get(key);
        if (actualValue != null) {
            root = remove(root, key);
        }
        return actualValue;
    }

    @Override
    public V remove(K key, V value) {
        V actualValue = get(key);
        if (actualValue != null && actualValue.equals(value)) {
            root = remove(root, key);
        }
        return actualValue;
    }

    @Override
    public Iterator<K> iterator() {

        return new BSTMapIterator();
    }

    private class BSTMapIterator implements Iterator<K> {
        private final Stack<BSTNode<K, V>> stack;

        public BSTMapIterator() {
            stack = new Stack<>();
            pushLeft(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }

            BSTNode<K, V> node = stack.pop();
            if (node.right != null) {
                pushLeft(node.right);
            }
            return node.key;
        }

        private void pushLeft(@Nullable BSTNode<K, V> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(@Nullable BSTNode<K, V> node) {
        if (node == null) {
            return;
        }

        printInOrder(node.left);
        System.out.println(node);
        printInOrder(node.right);
    }

    /**
     * Insert a key-value pair into a node-rooted binary search tree.
     *
     * @return The root of the binary search tree after inserting a new node.
     */
    private @Nonnull BSTNode<K, V> put(@Nullable BSTNode<K, V> node, K key, V value) {
        if (node == null) {
            return new BSTNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        }

        return node;
    }


    private @Nullable V get(@Nullable BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    private int size(@Nullable BSTNode<K, V> node) {
        if (node == null) {
            return 0;
        }

        return size(node.left) + size(node.right) + 1;
    }

    private boolean containsKey(@Nullable BSTNode<K, V> node, K key) {
        if (node == null) {
            return false;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return containsKey(node.left, key);
        } else if (cmp > 0) {
            return containsKey(node.right, key);
        } else {
            return true;
        }
    }

    /**
     * @return The largest node of the binary search tree is rooted.
     */
    private @Nullable BSTNode<K, V> max(@Nullable BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }

        if (node.right == null) {
            return node;
        }

        return max(node.right);
    }

    /**
     * @return The smallest node of the binary search tree is rooted.
     */
    private @Nullable BSTNode<K, V> min(@Nullable BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node;
        }

        return min(node.left);
    }

    /**
     * Remove a given element from a binary search tree rooted in a node.
     *
     * @return The root of the BST after removal.
     */
    private @Nullable BSTNode<K, V> remove(@Nullable BSTNode<K, V> node, @Nonnull K key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
            return node;
        }

        if (cmp > 0) {
            node.right = remove(node.right, key);
            return node;
        }

        if (node.left == null && node.right == null) {
            return null;
        }

        if (node.left == null) {
            return node.right;
        }

        if (node.right == null) {
            return node.left;
        }

        BSTNode<K, V> successor = Objects.requireNonNull(min(node.right));
        node.key = successor.key;
        node.value = successor.value;
        node.right = remove(node.right, successor.key);
        return node;
    }
}
