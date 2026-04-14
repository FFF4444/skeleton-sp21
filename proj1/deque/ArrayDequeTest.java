package deque;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void  Test(){
        ArrayDeque<Integer> MyList1 = new ArrayDeque<>();
        LinkedListDeque<Integer> MyList2 = new LinkedListDeque<>();
        int N=500000;
        for(int i=0;i<N;i++){
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                MyList1.addLast(randVal);
                MyList2.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                assertEquals(MyList1.size(),MyList2.size());
            }else if(operationNumber == 2){
                int randVal = StdRandom.uniform(0, 100);
                MyList1.addFirst(randVal);
                MyList2.addFirst(randVal);
            }else if (MyList1.size()!=0 && MyList2.size()!=0){
                if(operationNumber==3){
                    int randVal = StdRandom.uniform(0, MyList1.size());
                    assertEquals(MyList1.get(randVal),MyList2.get(randVal));
                }else if(operationNumber==4){
                    assertEquals(MyList1.removeFirst(),MyList2.removeFirst());
                } else if (operationNumber == 5) {
                    assertEquals(MyList1.removeLast(),MyList2.removeLast());
                }
            }
        }
        int index = 0;
        for(int x:MyList1){
            assertEquals(x,(int)MyList2.get(index));
            index++;
        }
        MyList2.printDeque();
        MyList1.printDeque();
    }
}
