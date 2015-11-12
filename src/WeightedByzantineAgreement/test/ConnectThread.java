package WeightedByzantineAgreement.test;

/**
 * Created by wenwen on 11/12/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectThread extends Thread {
    private int numProcess;
    private int port;
    static BufferedReader[] dataIn;
    static PrintWriter[] dataOut;
    private Socket[] socket;
    static BlockingQueue<MessageTest> list;


    public ConnectThread(int num, int myPort){
        this.port = myPort;
        this.numProcess = num;
        this.dataIn = new BufferedReader[num];
        this.dataOut = new PrintWriter[num];
        this.list = new LinkedBlockingQueue<MessageTest>();
        this.start();

    }
    public void setSocket(int Id, Socket s){
        socket[Id] = s;
    }

    public void run(){
        try {
            ServerSocket serSocket = new ServerSocket(port);

            while(true){

                Socket s = serSocket.accept();
                BufferedReader data = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String read = data.readLine();
                MessageTest msgIn = new MessageTest();
                msgIn.parseMsg(read);
                if(msgIn.retTag() == "Initiating"){
                    setSocket(msgIn.retSrcId(),s);
                    dataOut[msgIn.retSrcId()] = new PrintWriter(s.getOutputStream());
                    new ThreadChannelTest(data).start();
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
