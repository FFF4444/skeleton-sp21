package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {

    private T[] array = (T[]) new Object[8];
    private int size = 0, head, tail;

    private void resize(int newSize) {
        T[] newArray = (T[]) new Object[newSize];
        int j = 0, i;
        for (i = head; j < size; j++, i = (i + 1) % array.length) {
            newArray[j] = array[i];
        }
        array = newArray;
        head = 0;
        tail = size - 1;
    }

    @Override
    public void addFirst(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        size++;
        if (size == 1) {
            array[0] = item;
            head = 0;
            tail = 0;
            return;
        }
        head--;
        if (head == -1) {
            head = array.length - 1;
        }
        array[head] = item;
    }

    @Override
    public void addLast(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        size++;
        if (size == 1) {
            array[0] = item;
            head = 0;
            tail = 0;
            return;
        }
        tail = (tail + 1) % array.length;
        array[tail] = item;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int j = 0, i;
        for (i = head; j < size; j++, i = (i + 1) % array.length) {
            if (j == size - 1) {
                System.out.println(array[i]);
            }  else {
                System.out.print(array[i] + " ");
            }
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size - 1 < array.length / 4) {
            resize(size);
        }
        T pop = array[head];
        head = (head + 1) % array.length;
        size--;
        return pop;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size - 1 < array.length / 4) {
            resize(size);
        }
        T pop = array[tail];
        tail--;
        if (tail == -1) {
            tail = array.length - 1;
        }
        size--;
        return pop;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(head + index) % array.length];
    }

    private class seer implements Iterator<T> {

        int cur = head, count = 0;
        @Override
        public boolean hasNext() {
            return count != size;
        }

        @Override
        public T next() {
            T result = array[cur];
            cur = (cur + 1) % array.length;
            count++;
            return result;
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
