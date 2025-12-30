package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author huang.kai
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int RESIZE_FACTOR = 2;

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private final double maxLoad;
    private int size;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
        this.size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        @SuppressWarnings("unchecked")
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    @Override
    public void clear() {
        buckets = createTable(buckets.length);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        var bucket = getBucket(key);
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        var bucket = getBucket(key);
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (loadFactor() >= maxLoad) {
            resize();
        }

        var bucket = getBucket(key);
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (K key: this) {
            keySet.add(key);
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        var bucket = getBucket(key);
        for (Node node: bucket) {
            if (node.key.equals(key)) {
                bucket.remove(node);
                size--;
                return node.value;
            }
        }

        return null;
    }

    @Override
    public V remove(K key, V value) {
        var bucket = getBucket(key);
        for (Node node: bucket) {
            if (node.key.equals(key) && node.value.equals(value)) {
                bucket.remove(node);
                size--;
                return node.value;
            }
        }

        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        private int bucketIndex;
        private Iterator<Node> currentBucketIterator;

        public MyHashMapIterator() {
            bucketIndex = 0;
            currentBucketIterator = null;
            findNextBucket();
        }

        @Override
        public boolean hasNext() {
            if (currentBucketIterator == null) {
                return false;
            }

            if (currentBucketIterator.hasNext()) {
                return true;
            }

            bucketIndex++;
            findNextBucket();
            return currentBucketIterator != null && currentBucketIterator.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }

            return currentBucketIterator.next().key;
        }

        private void findNextBucket() {
            while (bucketIndex < buckets.length) {
                if (buckets[bucketIndex] != null && !buckets[bucketIndex].isEmpty()) {
                    currentBucketIterator = buckets[bucketIndex].iterator();
                    return;
                }
                bucketIndex++;
            }
            currentBucketIterator = null;
        }
    }

    private double loadFactor() {
        return (double) size / buckets.length;
    }

    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }

    private Collection<Node> getBucket(K key) {
        int index = getIndex(key);
        return buckets[index];
    }

    private void resize() {
        int newSize = buckets.length * RESIZE_FACTOR;
        MyHashMap<K, V> hashMap = new MyHashMap<>(newSize, maxLoad);
        for (Collection<Node> bucket: buckets) {
            for (Node node: bucket) {
                hashMap.put(node.key, node.value);
            }
        }

        buckets = hashMap.buckets;
    }

}
