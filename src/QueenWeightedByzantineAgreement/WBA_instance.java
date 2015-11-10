package QueenWeightedByzantineAgreement;

import com.sun.corba.se.pept.transport.ListenerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

/**
 * Created by apple on 11/8/15.
 */
public class WBA_instance {
    static PrintWriter[] dataOut;
    static BufferedReader[] dataIn;

    private int process_id;
    private int num_processes;
    private int port[];



    public WBA_instance(int process_id,int num){
        this.process_id = process_id;
        this.num_processes = num;
        for(int i = 0 ; i< num; i++){
            this.port[i] = 8000+i;

        }


    }
    public static void main(String args[]){
        WBA_instance wba_instance  = new WBA_instance(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        Queen_WBA msg_agreement = new Queen_WBA(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        int flag = 0;



            try {

                ServerSocket listener = new ServerSocket(wba_instance.port[wba_instance.process_id]);
                for(int i = 0; i < wba_instance.process_id; i++) {
                    Socket s = listener.accept();
                    s.setSoTimeout(15*1000);
                    BufferedReader dIn = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));
                    PrintWriter dOut = new PrintWriter(s.getOutputStream());

                    String getLine = dIn.readLine();
                    StringTokenizer st = new StringTokenizer(getLine);
                    int fromProcessId = Integer.parseInt(st.nextToken());
                    String round = st.nextToken();
                    if (round.equals("say hello")) {

                        dataOut[fromProcessId] = dOut;
                        dataIn[fromProcessId] = dIn;

                        //Start thread to listen on this port

                    }

                    new ThreadChannel(fromProcessId,msg_agreement).start();
                    flag = flag + 1;

                }

            }catch (IOException e){
                e.printStackTrace();
            }





        for (int i =  wba_instance.process_id+ 1; i < wba_instance.num_processes; i++) {
            Socket s = null;
            //try to connect to process i, until the connection is made
            while(s == null ){
                try{
                    s = new Socket("127.0.0.1", 8000 + i);
                    s.setSoTimeout(15*1000);
                    PrintWriter dOut = new PrintWriter(s.getOutputStream());
                    BufferedReader dIn = new BufferedReader(new
                            InputStreamReader(s.getInputStream()));
                    dOut.println(wba_instance.process_id + " " + "say hello" + " " + null);
                    dOut.flush();
                    dataIn[i] = dIn;
                    dataOut[i] = dOut;
                    new ThreadChannel(i,msg_agreement).start();
                    flag = flag + 1;

                } catch(Exception e)
                {e.printStackTrace();}
            }

        }

        if(flag == wba_instance.num_processes-1)
        new ThreadProcess(msg_agreement).start();


    }
}
