package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private int size = 16, items = 0;
    private double loadFactor = 0.75;
    /* Instance Variables */
    private Collection<Node>[] buckets = createTable(size);
    // You should probably define some more!
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


    /** Constructors */
    public MyHashMap() {
    }

    public MyHashMap(int initialSize) {
        size = initialSize;
        buckets = createTable(size);
    }
    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return  new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private int hash(K key) {
        int result = key.hashCode();
        return Math.floorMod(result, size);
    }

    @Override
    public void clear() {
        items = 0;
        buckets = createTable(size);
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int h = hash(key);
        if (buckets[h] != null) {
            for (Node i : buckets[h]) {
                if (i.key.equals(key)) {
                    return i.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return items;
    }

    private void resize(int newSize) {
        Collection<Node>[] tmp = buckets;
        buckets = createTable(newSize);
        size = newSize;
        items = 0;
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null) {
                for (Node j : tmp[i]) {
                    put(j.key, j.value);
                }
            }
        }
    }
    @Override
    public void put(K key, V value) {
        if ((items + 1) / size > loadFactor) {
            resize(2 * size);
        }
        items++;
        int h = hash(key);
        Node n = new Node(key, value);
        remove(key);
        if (buckets[h] == null) {
            buckets[h] = createBucket();
            buckets[h].add(n);
            return;
        }
        buckets[h].add(n);

    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if (buckets[i] != null) {
                for (Node j : buckets[i]) {
                    set.add(j.key);
                }
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        int h = hash(key);
        if (buckets[h] != null) {
            for (Node x : buckets[h]) {
                if (x.key.equals(key)) {
                    V result = x.value;
                    buckets[h].remove(x);
                    items--;
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        LinkedList<K> list = new LinkedList();
        for (int i = 0; i < size; i++) {
            if (buckets[i] != null) {
                for (Node j : buckets[i]) {
                    list.add((K) j.key);
                }
            }
        }
        return list.iterator();
    }
}
