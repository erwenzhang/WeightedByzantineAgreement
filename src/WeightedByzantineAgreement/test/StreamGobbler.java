package WeightedByzantineAgreement.test;

/**
 * Created by apple on 11/13/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamGobbler extends Thread {

    private InputStream is;
    private int pid;

    public StreamGobbler(int pid, InputStream is) {
        this.pid = pid;
        this.is = is;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null)
                System.out.println("pid " + pid + "> " + line);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}