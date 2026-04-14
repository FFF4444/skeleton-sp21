package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        cmp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }
        T maxItem = this.get(0);
        for (T x: this) {
            if (cmp.compare(x,maxItem) > 0) {
                maxItem = x;
            }
        }
        return maxItem;
    }

    public  T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T maxItem = this.get(0);
        for (T x: this) {
            if (c.compare(x,maxItem) > 0) {
                maxItem = x;
            }
        }
        return maxItem;
    }

}
