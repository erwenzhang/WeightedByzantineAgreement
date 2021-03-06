package WeightedByzantineAgreement.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wenwen on 11/10/15.
 */
public class Tester {

    private static final int num_processes = 10;
    private static int correct_pro;
    private static String current_dir = "/Users/apple/Documents/myjava/QueenWeightedByzantineAgreement/bin";
    private static String cp ="/Users/apple/Documents/myjava/QueenWeightedByzantineAgreement/failure_probability.txt";

    private static int tester_port = 7999;
    private static int wait_max = 30;
    private static double[] weight;
    private static boolean[] type;
    private static double[] failure_probability;

    static Stopwatch stopwatch = new Stopwatch();

    public static Process processes[];

    public static void main(String[] args) throws Exception{
         processes = new Process[num_processes];
         weight = new double[num_processes];
        type = new boolean[num_processes];
        failure_probability = new double[num_processes];

        System.out.println("Start launching " + num_processes + " WBA processes.");
         launch_processes();

        //listen for processes hello and after listening, start a new thread to accept msgs from that process
        ConnectThread connectionManager = new ConnectThread(num_processes,tester_port);


        System.out.println("Finished launching " + num_processes + " WBA processes.");

        // assign_type, assign_weight,
        assign_type();
        assign_weight();


        System.out.println("Waiting for 'ready' message from all launched proceseses");
        int ReadysReceived = 0;

        // Start waiting for ready msg from subprocesses
        stopwatch.start();

        while(ReadysReceived < num_processes && (stopwatch.getElapsedTimeSecs() < wait_max)) {
            MessageTest msg = connectionManager.getMessage();
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

        // send type and weight to subprocess
        for (int i = 0; i<num_processes;i++){
            connectionManager.sendMessages(i,"type",Boolean.toString(type[i]));
            String sending_weight = Arrays.toString(weight).replaceAll("\\s+","");
            connectionManager.sendMessages(i, "weight", sending_weight);

            System.out.println("send type_weight to subprocess  " + sending_weight);
        }


        //Start agreement processes
        stopwatch.start();

        for(int i = 0; i<num_processes;i++){
            connectionManager.sendMessages(i, "start", "null");
            System.out.println("send start to subprocess");
        }

        System.out.println("Waiting for decide messages from all launched processes.");
        int DecideReceived =  0;
        ArrayList<Integer> decisions = new ArrayList<Integer>();
        while((DecideReceived<correct_pro)){
            MessageTest msg = connectionManager.getMessage();
            if(msg!=null){
                if(msg.retTag().equals("decide")){
                    DecideReceived++;
                    decisions.add(Integer.parseInt(msg.retInfo()));
                    System.out.println("Received decide from "+Integer.toString(msg.retSrcId()));
                }
            }
        }
        stopwatch.stop();

        if (DecideReceived == correct_pro) {
            System.out.println("Received 'decide' from all " + correct_pro +" processes.");
            System.out.println("Agreement took " + stopwatch.getElapsedTime() + " ms");
        }
        else {
            System.out.println("Agreement failed. Only received " + DecideReceived + "'decide' messages.");
        }

        if(decisions.size() > 0) {
            int first = decisions.get(0);

            boolean match = true;
            for(int i = 1; i < decisions.size(); i++) {
             //   System.out.println("Received decide from "+Integer.toString(i));
                if(decisions.get(i) != first) {
                    match = false;
                    break;
                }
            }
            if(match) {
                System.out.println("All processes decided the same value: " + first);
            }
            else {
                System.out.print("The processes did not all decide the same value:");
                System.out.println(decisions);
            }
        }

        kill_processes(processes);













    }

    private static void assign_weight(){
        double fail_weight  = 0;
        double []tmp = new double[num_processes];
        for(int i = 0; i<num_processes; i++){
            tmp[i]= 1.0/failure_probability[i];
        }
        double  sum = 0.0 ;
        for(int i = 0; i<num_processes; i++){
            sum += tmp[i];
        }
        for(int i = 0; i<num_processes; i++){
            weight[i]= (1.0/sum)*tmp[i];
            if(!type[i]){
                fail_weight = fail_weight + weight[i];
            }
        }
        System.out.println("fail_weight "+fail_weight);
    }

    private static void assign_type(){
        File file = new File(cp);
        BufferedReader reader = null;
        correct_pro = 0;

        int count = 0;
        try{
            reader = new BufferedReader(new FileReader(file));

            String tmpDouble = reader.readLine();
            tmpDouble = tmpDouble.substring(1,tmpDouble.length()-1);
            if(tmpDouble!=null){
                for (String tmp:tmpDouble.split(",")){
                    if(Double.parseDouble(tmp)<0)
                    failure_probability[count] =0.01 ;
                    else if(Double.parseDouble(tmp)>=1){
                        failure_probability[count] = 0.99;
                    }
                    else
                    failure_probability[count] =Double.parseDouble(tmp);

                    System.out.println(failure_probability[count]);
                    if( Math.random()>failure_probability[count]){
                        type[count] = true;
                        correct_pro++;
                    }
                    else
                        type[count] = false;
                    System.out.println(type[count]);
                    count++;
                }
             //   failure_probability[count] = Double.parseDouble(tmpDouble);

            }
            reader.close();
        }catch (IOException e){
            System.out.println("error from assign_type :" + e);
        }
    }

    private static void launch_processes(){
        File log = new File("log.txt");
        try {
            for(int i = 0;i<num_processes;i++){
          //  int i = 0;
                System.out.println("start launching process "+i);
                ProcessBuilder process_builder = new ProcessBuilder("java","WeightedByzantineAgreement/algorithm/WBA_instance",Integer.toString(i),Integer.toString(num_processes),Integer.toString(tester_port));
            //   ProcessBuilder process_builder = new ProcessBuilder( "/Users/apple/Documents/myjava/test.bat");

                process_builder.directory(new File(current_dir));
                process_builder.redirectErrorStream(true);
                process_builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                processes[i] = process_builder.start();

               // StreamGobbler sg = new StreamGobbler(i, processes[i].getInputStream());
               // sg.start();

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
