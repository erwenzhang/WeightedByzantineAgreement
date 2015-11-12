package WeightedByzantineAgreement.test;

/**
 * Created by wenwen on 11/12/15.
 */

import java.util.concurrent.TimeUnit;

public class MessageTransTest {

    private int myId;
    private int numProc;


    public MessageTransTest(int ID, int num){
        myId = ID;
        numProc = num;

    }

    /*Message Format: myId, V */
    public  void sendMessages(int destId,String tag, String info){
        MessageTest transInfo = new MessageTest(myId,tag,(byte)Integer.parseInt(info));
        ConnectThread.dataOut[destId].println(transInfo.formString());
        ConnectThread.dataOut[destId].flush();

    }

    public MessageTest getMessage() throws InterruptedException{
        return ConnectThread.list.poll(2, TimeUnit.SECONDS);
    }

}
