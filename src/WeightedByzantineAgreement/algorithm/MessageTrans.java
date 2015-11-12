package WeightedByzantineAgreement.algorithm;

import WeightedByzantineAgreement.algorithm.Messages;
import WeightedByzantineAgreement.algorithm.WBA_instance;

public class MessageTrans {

    private int myId;
    private int numProc;
   // private Socket[] sockets;
   // private BufferedReader[] DataIn;
  //  private PrintWriter[] DataOut;

    public MessageTrans(int ID, int num){
        myId = ID;
        numProc = num;
    //    sockets = new Socket[numProc];
       // DataIn = new BufferedReader[numProc];
        //DataOut = new PrintWriter[numProc];
    }

    /*Message Format: myId, V */
    public  void sendMessages(int destId,String tag, String info){
        if(myId != destId){
            Messages transInfo = new Messages(myId,tag,info);
            WBA_instance.dataOut[destId].println(transInfo.formString());
            WBA_instance.dataOut[destId].flush();
        }
    }
}
