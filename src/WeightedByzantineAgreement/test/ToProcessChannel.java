package WeightedByzantineAgreement.test;

/**
 * Created by wenwen on 11/12/15.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
        import java.net.SocketTimeoutException;
        import java.util.*;

public class ToProcessChannel extends Thread {
    private  BufferedReader message;


    public ToProcessChannel(BufferedReader data){
        this.message = data;
    }

    public void run() {

        while(true)
            try {

                String s = message.readLine();
                MessageTest msgIn = new MessageTest();
                msgIn.parseMsg(s);
                ConnectThread.list.put(msgIn.formMsg());
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

