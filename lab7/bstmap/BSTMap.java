package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }
    }
    private BSTNode root = null;
    private int size = 0;
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (traHelper(key, root) == null) {
            return false;
        };
        return true;
    }

    @Override
    public V get(K key) {
        BSTNode target = traHelper(key, root);
        if (target == null) {
            return null;
        }
        return target.value;
    }

    private BSTNode traHelper(Object key, BSTNode root) {
        if (root == null) {
            return null;
        }
        int x = root.key.compareTo((K) key);
        if (x == 0) {
            return root;
        } else if (x > 0) {
            return traHelper(key, root.left);
        } else {
            return traHelper(key, root.right);
        }
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
        size++;
    }

    private BSTNode putHelper(K key, V value, BSTNode root) {
        if (root == null) {
            root = new BSTNode(key, value);
        }
        int x = root.key.compareTo(key);
        if (x > 0) {
            root.left = putHelper(key, value, root.left);
        } else if (x < 0) {
            root.right = putHelper(key, value, root.right);
        } else {
            root.value = value;
        }
        return root;
    }

    @Override
    public Set keySet() {
        return setHelper(root);
    }

    public Set setHelper(BSTNode root) {
        HashSet<K> result = new HashSet<>();
        if (root == null) {
            return result;
        }
        result.addAll(setHelper(root.left));
        result.add(root.key);
        result.addAll(setHelper(root.right));
        return result;
    }
    @Override
    public V remove(K key) {
        BSTNode target = traHelper(key, root);
        if (target == null) {
            return null;
        }
        size--;
        root = removeHelper(key, root);
        return target.value;
    }

    private BSTNode removeHelper(K key, BSTNode root) {
        int x = key.compareTo(root.key);
        if (root == null) {
            return null;
        }
        if (x > 0) {
            root.right = removeHelper(key, root.right);
        } else if (x < 0) {
            root.left = removeHelper(key, root.left);
        } else {
            if (root.right == null) {
                root = root.left;
            } else if (root.left == null) {
                root = root.right;
            } else {
                BSTNode min = minNode(root.right);
                root.key = min.key;
                root.value = min.value;
                root.right = removeHelper(root.key, root.right);
            }
        }
        return root;
    }
    private BSTNode minNode(BSTNode root) {
        if (root == null || root.left == null) {
            return root;
        }
        return minNode(root.left);
    }

    @Override
    public V remove(K key, V value) {
        BSTNode target = traHelper(key, root);
        if (target == null || target.value != value) {
            return null;
        }
        size--;
        root = removeHelper(key, root);
        return target.value;
    }

    @Override
    public Iterator iterator() {
        return new iter();
    }
    private class iter<K> implements Iterator<K> {
        private Iterator list;
        public iter(){
            List<K> head = new ArrayList<>();
            keyList(root, head);
            list = head.listIterator();
        }
        private void keyList(BSTNode root, List<K> head) {
            if (root == null) {
                return;
            }
            keyList(root.left, head);
            head.add((K) root.key);
            keyList(root.right, head);
        }
        @Override
        public boolean hasNext() {
            return list.hasNext();
        }

        @Override
        public K next() {
            return (K) list.next();
        }
    }
}
