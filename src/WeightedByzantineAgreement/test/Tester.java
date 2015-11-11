package WeightedByzantineAgreement.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenwen on 11/10/15.
 */
public class Tester {

    private static final int num_processes = 50;
    private static String current_dir = "/Users/apple/Documents/myjava/QueenWeightedByzantineAgreement/bin";

    private static int tester_port = 7999;
    private static int wait_max = 30;

    static Stopwatch stopwatch = new Stopwatch();

    public static Process processes[];

    public static void main(String args[]) throws Exception{
         processes = new Process[num_processes];










    }

    public static void launch_process(){
        for(int i = 0;i<num_processes;i++){
            ProcessBuilder process_builder = new ProcessBuilder("java","WeightedByzantineAgreement/algorithm/WBA_instance",Integer.toString(i),Integer.toString(num_processes),Integer.toString(tester_port));
            process_builder.directory(new File(current_dir));
            process_builder.redirectErrorStream(true);
            processes[i] = process_builder.start();
            File log = new File("log");
            process_builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

        }
    }





}
