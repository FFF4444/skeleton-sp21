package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void  test() {
        StudentArrayDeque<Integer> myList1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> myList2 = new ArrayDequeSolution<>();
        int N = 500;
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                myList1.addLast(randVal);
                myList2.addLast(randVal);
                message.append("addLast(" + randVal + ")\n");
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                //message.append("size()\n");
                assertEquals(message.toString(), myList1.size(), myList2.size());
            } else if (operationNumber == 2) {
                int randVal = StdRandom.uniform(0, 100);
                message.append("addFirst(" + randVal + ")\n");
                myList1.addFirst(randVal);
                myList2.addFirst(randVal);
            } else if (myList1.size() != 0 && myList2.size() != 0) {
                if (operationNumber == 3) {
                    int randVal = StdRandom.uniform(0, myList1.size());
                    //message.append("get(" + randVal + ")\n");
                    assertEquals(message.toString(), myList1.get(randVal), myList2.get(randVal));
                } else if (operationNumber == 4) {
                    message.append("removeFirst()\n");
                    assertEquals(message.toString(), myList1.removeFirst(), myList2.removeFirst());
                } else if (operationNumber == 5) {
                    message.append("removeLast()\n");
                    assertEquals(message.toString(), myList1.removeLast(), myList2.removeLast());
                }
            }
        }
        myList2.printDeque();
        myList1.printDeque();
    }
}
