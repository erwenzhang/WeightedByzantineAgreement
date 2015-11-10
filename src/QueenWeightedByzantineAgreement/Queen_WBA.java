package QueenWeightedByzantineAgreement;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Random;


public class Queen_WBA {
    private int numProc ;
    private final byte undecided = -1;
    private int myId;
    private volatile byte V;
    private volatile ArrayList<Double> w;
    private volatile int anchor;
    protected ArrayList<ArrayList<Byte>> Queue;

    public Queen_WBA(int ID, int Process){

        myId = ID;
        numProc = Process;
        w = new ArrayList<Double>(numProc);
        Queue = new ArrayList<ArrayList<Byte>>(numProc);
        Random rand = new Random();
        V = (byte) rand.nextInt(2);
        assignW();
        anchor = assignAnchor();
    }

    public void procMsg(String line){
        System.out.println(line);
        Messages rcvMsg = new Messages();
        rcvMsg.parseMsg(line);
        if(rcvMsg.retTag().equals("V")){
            synchronized(Queue.get(rcvMsg.retSrcId())){
                Queue.get(rcvMsg.retSrcId()).add(rcvMsg.retInfo());
            }

        }
        else if(rcvMsg.retTag().equals("QueenValue")){
            synchronized(Queue.get(rcvMsg.retSrcId())){
                Queue.get(rcvMsg.retSrcId()).add(rcvMsg.retInfo());
            }
        }
    }

    public int assignAnchor(){
        double bound = 0.0;
        int Anchor = 0;
        ArrayList<Double> tmp_w = new ArrayList<Double>(w);
        Collections.sort(tmp_w);
        for(int i = numProc-1; i > -1 ; i--){
            bound = bound + tmp_w.get(i);
            if(bound > 1.0/4.0){
                Anchor= numProc - i;
                break;
            }
        }
        return Anchor;
    }

    public void assignW(){
        for(int i = 0; i< numProc; i++){
            w.set(i, ((double)1/(double)numProc));
        }
    }

    public void processing(){
        for(int i = 0; i<anchor; i++){
            double s0 = 0.0, s1 =0.0;
            double myWeight;
            byte myValue;
            MessageTrans trans = new MessageTrans(myId,numProc);
/*first phase*/
		 /*send message to all other process except myself*/
            if(w.get(myId) > 0){
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"V", Integer.toString(V));
                }
            }

		 /*receive message*/
            Queue.get(myId).add(V);
            for(int j= 0; j<numProc; j++){
                if(w.get(j)>0){
                    boolean receiveMsg = false;
                    byte getValue = -1;
                    while(!receiveMsg){
                        synchronized(Queue.get(j)){
                            if(!Queue.get(j).isEmpty()){
                                getValue = Queue.get(j).get(0);
                                Queue.get(j).remove(0);
                                receiveMsg = true ;
                            }
                        }

                    }
                    if(getValue == 1){
                        s1 = s1 + w.get(j);
                    }
                    if(getValue == 0){
                        s0 = s0 + w.get(j);
                    }
                }

            }

	      /*update value*/
            if(s1 >= 1.0/2.0){
                myValue = 1;
                myWeight = s1;
            }
            else {
                myValue = 0;
                myWeight = s0;
            }

/*second phase*/
            byte QueenValue = -1;
            if(i == myId){
                V = myValue;
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"QueenValue", Integer.toString(V));
                }
            }
            else{
                boolean receiveMsg = false;

                while(!receiveMsg){
                    synchronized(Queue.get(i)){
                        if(!Queue.get(i).isEmpty()){
                            QueenValue = Queue.get(i).get(0);
                            Queue.get(i).remove(0);
                            receiveMsg = true ;
                        }
                    }

                }
                if(myWeight > (3.0/4.0)){
                    V = myValue;
                }
                else V = QueenValue;
            }
            System.out.println("Final V = "+Integer.toString(V));
        }
    }
}
