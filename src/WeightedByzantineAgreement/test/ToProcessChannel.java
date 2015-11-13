package WeightedByzantineAgreement.test;

/**
 * Created by wenwen on 11/12/15.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
        import java.net.SocketTimeoutException;
        import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class ToProcessChannel extends Thread {
    private  BufferedReader message;
    private BlockingQueue<MessageTest> queue;


    public ToProcessChannel(BufferedReader data,BlockingQueue<MessageTest> queue){
        this.message = data;
        this.queue = queue;
    }

    public void run() {

        while(true)
            try {

                String s = message.readLine();
                MessageTest msgIn = new MessageTest();
                msgIn.parseMsg(s);
                queue.put(msgIn.formMsg());
            } catch (SocketException e) {
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }


}

