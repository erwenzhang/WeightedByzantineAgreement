package QueenWeightedByzantineAgreement;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;
public class MessageTrans {

    private int myId;
    private int numProc;
   // private Socket[] sockets;
   // private BufferedReader[] DataIn;
  //  private PrintWriter[] DataOut;
 Process process[] = new Process[50];
    ProcessBuilder pb =
            StreamGobbler

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
            Messages transInfo = new Messages(myId,tag,(byte)Integer.parseInt(info));
            WBA_instance.dataOut[destId].println(transInfo.formString());
            WBA_instance.dataOut[destId].flush();
        }
    }
}
