package WeightedByzantineAgreement.test;

import java.io.File;
import java.util.ArrayList;

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

    public static void main(String[] args) throws Exception{
         processes = new Process[num_processes];
        System.out.println("Start launching " + num_processes + " WBA processes.");
         launch_processes();
        System.out.println("Finished launching " + num_processes + " WBA processes.");

        System.out.println("Waiting for 'ready' message from all launched proceseses");
        int ReadysReceived = 0;

        // Start waiting for ready msg from subprocesses
        stopwatch.start();

        while(ReadysReceived < num_processes && (stopwatch.getElapsedTimeSecs() < wait_max)) {
            MessageTest msg = .getNextMessage();
            if(msg != null) {
                if(msg.retTag().equals("ready")) {
                    ReadysReceived++;
                }
            }
        }
        if (ReadysReceived == num_processes){
            System.out.println("Received 'ready' from all processes.");
        }
        else{
            System.out.println("Only received 'ready' from " + ReadysReceived + " processes.");
            System.out.println("Exiting.");
            kill_processes(processes);
        }

        //Start agreement processes
        stopwatch.start();

        for(int i = 0; i<num_processes;i++){
            MessageTransTest.sendMessages(i, "start", "null");
        }

        System.out.println("Waiting for decide messages from all launched processes.");
        int DecideReceived =  0;
        ArrayList<Integer> decisions = new ArrayList<Integer>();
        while((DecideReceived<num_processes)&&stopwatch.getElapsedTime()<wait_max){
            MessageTest msg = MessageTransTest.getNextMessage();
            if(msg!=null){
                if(msg.retTag().equals("decided")){
                    DecideReceived++;
                    decisions.add(msg.retInfo().intValue());
                }
            }
        }
        stopwatch.stop();














    }

    private static void launch_processes(){
        try {
            for(int i = 0;i<num_processes;i++){
                ProcessBuilder process_builder = new ProcessBuilder("java","WeightedByzantineAgreement/algorithm/WBA_instance",Integer.toString(i),Integer.toString(num_processes),Integer.toString(tester_port));
                process_builder.directory(new File(current_dir));
                process_builder.redirectErrorStream(true);
                processes[i] = process_builder.start();
                File log = new File("log");
                process_builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

            }
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private static void kill_processes(Process[] process){
        System.out.println("Killing all proceses.");
        for(int i = 0; i < num_processes; i++) {
            if(processes[i] != null )
                processes[i].destroy();
        }
        System.out.println("Finished killing all WBGA proceses.");

        System.exit(0);

    }





}
