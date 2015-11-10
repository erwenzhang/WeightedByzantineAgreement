/**
 * Created by apple on 11/8/15.
 */
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class compute_afa{
    public static void main(String args[]) {

        ArrayList<Double> w = new ArrayList<Double>(Arrays.asList(0.1,0.1,0.1,0.1,0.1,0.2,0.1,0.1,0.1));
        double bound = 0.0;
        int afa = 0;
        ArrayList<Double> tmp_w = new ArrayList<Double>(w);
        Collections.sort(tmp_w);
        for (int i = 8; i > -1; i--) {
            bound = bound + tmp_w.get(i);
            System.out.println(bound);
            if (bound > 1.0 / 2.0) {
                afa = 9 - i;
                System.out.println(afa);
                break;

            }
        //    System.out.println(tmp_w.get(i));
           // System.out.print(w.get(i));
        }
    }

}