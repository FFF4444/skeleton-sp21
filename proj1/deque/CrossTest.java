package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

public class CrossTest {
    @Test
    public void  test() {
        ArrayDeque<Integer> myList1 = new ArrayDeque<>();
        LinkedListDeque<Integer> myList2 = new LinkedListDeque<>();
        int N=500000;
        for (int i=0;i<N;i++) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                myList1.addLast(randVal);
                myList2.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                assertEquals(myList1.size(),myList2.size());
            } else if (operationNumber == 2) {
                int randVal = StdRandom.uniform(0, 100);
                myList1.addFirst(randVal);
                myList2.addFirst(randVal);
            } else if (myList1.size() != 0 && myList2.size() != 0) {
                if (operationNumber == 3) {
                    int randVal = StdRandom.uniform(0, myList1.size());
                    assertEquals(myList1.get(randVal),myList2.get(randVal));
                } else if (operationNumber == 4) {
                    assertEquals(myList1.removeFirst(),myList2.removeFirst());
                } else if (operationNumber == 5) {
                    assertEquals(myList1.removeLast(),myList2.removeLast());
                }
            }
        }
        myList2.printDeque();
        myList1.printDeque();
    }
}
