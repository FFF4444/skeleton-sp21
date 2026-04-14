package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    private class Node {
        public T item = null;
        public Node prev = this;
        public Node next = this;

        public Node() {
        }

        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private int size = 0;
    private final Node head = new Node();

    @Override
    public void addFirst(T item) {
        Node Object = new Node(item, head, head.next);
        head.next.prev = Object;
        head.next = Object;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node Object = new Node(item, head.prev, head);
        head.prev.next = Object;
        head.prev = Object;
        size++;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node cur = head.next;
        for (int i = 0; i < size; i++, cur = cur.next) {
            if (i == size - 1) {
                System.out.println(cur.item);
            } else {
                System.out.print(cur.item + " ");
            }
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node pop = head.next;
        head.next = head.next.next;
        head.next.prev = head;
        size--;
        return pop.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node pop = head.prev;
        head.prev = head.prev.prev;
        head.prev.next = head;
        size--;
        return pop.item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        } else {
            Node cur = head.next;
            for (int i = 0; i < index; i++, cur = cur.next) { };
            return cur.item;
        }
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getHelper(index, head.next);
    }

    private T getHelper(int index, Node cur) {
        if (index == 0) {
            return cur.item;
        }
        return getHelper(index - 1, cur.next);
    }

    private class seer implements Iterator<T> {

        private Node cur = head.next;

        @Override
        public boolean hasNext() {
            return cur != head;
        }

        @Override
        public T next() {
            Node x = cur;
            cur = cur.next;
            return x.item;
        }

    }

    @Override
    public Iterator<T> iterator() {
        return new seer();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> other = (Deque<?>) o;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            T myItem = this.get(i);
            Object otherItem = other.get(i);
            if (myItem == null && otherItem != null) {
                return false;
            }
            if (myItem != null && !myItem.equals(otherItem)) {
                return false;
            }
        }
        return true;
    }

}