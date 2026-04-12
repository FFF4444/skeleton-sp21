package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE//
    @Test
    public void  TestBA(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N=500000;
        for(int i=0;i<N;i++){
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(),B.size());
            }else if (L.size()!=0 && B.size()!=0){
                if(operationNumber==2){
                    assertEquals(L.getLast(),B.getLast());
                }else if(operationNumber==3){
                    assertEquals(L.removeLast(),B.removeLast());
                }
            }
        }
    }
}
