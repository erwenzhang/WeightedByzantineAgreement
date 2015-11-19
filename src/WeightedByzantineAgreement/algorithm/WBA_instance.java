package WeightedByzantineAgreement.algorithm;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by wenwen on 11/8/15.
 */
public class WBA_instance {
    protected static PrintWriter[] dataOut;
    protected static BufferedReader[] dataIn;
    protected static PrintWriter dOutTester;
    protected static BufferedReader dInTester;


    private int process_id;
    private int num_processes;
    private int port[];
    private int tester_port;
    private int myport;
    private static final String local_host = "127.0.0.1";



    public WBA_instance(int process_id,int num, int tester_port){
        this.process_id = process_id;
        this.num_processes = num;
        this.myport = 8000+process_id;
        this.port = new int[num];
        for(int i = 0 ; i< num; i++){
            this.port[i] = 8000+i;
          //  System.out.println("port:" + Integer.toString(this.port[i]));

        }
        this.tester_port = tester_port;
        dataOut = new PrintWriter[num_processes];
        dataIn = new BufferedReader[num_processes];


    }
    public static void main(String[] args) throws Exception{
      //  System.out.println("before subprocesses started "+args[0]+" "+args[1]+" "+args[2]);
        WBA_instance wba_instance  = new WBA_instance(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        Queen_WBA msg_agreement = new Queen_WBA(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
       // int flag = 0;
       // KingWBAP msg_agreement = new KingWBAP(Integer.parseInt(args[0]),Integer.parseInt(args[1]));



            try {

                ServerSocket listener = new ServerSocket(wba_instance.myport);
               // System.out.println(Integer.toString(wba_instance.process_id)+" listen to lower process");
                for(int i = 0; i < wba_instance.process_id; i++) {
                    //System.out.println(wba_instance.myport);


                    Socket s = listener.accept();
                    s.setSoTimeout(3*1000);
                    BufferedReader dIn = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));
                    PrintWriter dOut = new PrintWriter(s.getOutputStream());

                    String getLine = dIn.readLine();
                    StringTokenizer st = new StringTokenizer(getLine);
                    int fromProcessId = Integer.parseInt(st.nextToken());
                    System.out.println(Integer.toString(wba_instance.process_id)+" has listened to lower process "+Integer.toString(fromProcessId));
                    String round = st.nextToken();
                    if (round.equals("hello1")) {

                        dataOut[fromProcessId] = dOut;
                        dataIn[fromProcessId] = dIn;

                        //Start thread to listen on this port

                    }

                    new ThreadChannel(wba_instance.process_id,fromProcessId,msg_agreement).start();
                    //flag = flag + 1;

                }

            }catch (IOException e){
                e.printStackTrace();
            }





        for (int i =  wba_instance.process_id+ 1; i < wba_instance.num_processes; i++) {
            Socket s = null;
          //  System.out.println(Integer.toString(wba_instance.process_id)+" try to socket to process " + Integer.toString(i));
           // System.out.println(i);
            //try to connect to process i, until the connection is made
            while(s == null ){
                try{
                    s = new Socket(local_host, 8000 + i);
                    s.setSoTimeout(3*1000);
                    PrintWriter dOut = new PrintWriter(s.getOutputStream());
                    BufferedReader dIn = new BufferedReader(new
                            InputStreamReader(s.getInputStream()));
                    dOut.println(wba_instance.process_id + " " + "hello1" + " " + "null");
               //     System.out.println("hello1");
                    dOut.flush();
                    dataIn[i] = dIn;
                    dataOut[i] = dOut;
                    new ThreadChannel(wba_instance.process_id,i,msg_agreement).start();
                 //   flag = flag + 1;

                } catch(Exception e)
                {e.printStackTrace();
                System.out.println(Integer.toString(wba_instance.process_id)+" failed to socket to process " + Integer.toString(i));}
            }

        }

        Socket testSocket = new Socket(local_host,wba_instance.tester_port);
        dOutTester = new PrintWriter(testSocket.getOutputStream());
        dInTester = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
        dOutTester.println(Integer.toString(wba_instance.process_id)+" "+"hello"+" "+"null");
        dOutTester.flush();
        new ToTesterChannel(wba_instance.process_id,msg_agreement).start();
        dOutTester.println(Integer.toString(wba_instance.process_id)+" "+"ready"+" "+"null");
        dOutTester.flush();

        // if(flag == wba_instance.num_processes-1)
      //  new ThreadProcess(msg_agreement).start();


    }
}
