package WeightedByzantineAgreement.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class KingWBAP {
    private int numProc ;
    private final String undecided = "-1";
    private int myId;
    private volatile String V;
    private volatile ArrayList<Double> w;
    private volatile int anchor;
    protected ArrayList<ArrayList<String>> Queue;

    public KingWBAP(int ID, int Process){
        myId = ID;
        numProc = Process;
        w = new ArrayList<Double>(numProc);
        Queue = new ArrayList<ArrayList<String>>(numProc);
        Random rand = new Random();
        V = Integer.toString(rand.nextInt(2));
        assignW();
        anchor = assignAnchor();
    }

    public void assignW(){
        for(int i = 0; i< numProc; i++){
            w.set(i, ((double)1/(double)numProc));
        }
    }

    public int assignAnchor(){
        double bound = 0.0;
        int Anchor = 0;
        ArrayList<Double> tmp_w = new ArrayList<Double>(w);
        Collections.sort(tmp_w);
        for(int i = numProc-1; i > -1 ; i--){
            bound = bound + tmp_w.get(i);
            if(bound > 1.0/3.0){
                Anchor= numProc - i;
                break;
            }
        }
        return Anchor;
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
        else if(rcvMsg.retTag().equals("KingValue")){
            synchronized(Queue.get(rcvMsg.retSrcId())){
                Queue.get(rcvMsg.retSrcId()).add(rcvMsg.retInfo());
            }
        }
    }


    public void processing(){

        for(int i = 0; i<anchor; i++){
            double s0 = 0.0, s1 =0.0, su = 0.0;
            double myWeight = w.get(myId);   //initialize w[myId];
            MessageTrans trans = new MessageTrans(myId,numProc);
/*first phase*/
            if(w.get(myId) > 0){
				 /*send message to all other process except myself*/
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j, "V", V);
                }
            }
		     /*receive message*/
            Queue.get(myId).add(V);
            for(int j= 0; j<numProc; j++){
                if(w.get(j)>0){
                    boolean receiveMsg = false;
                    String getValue = "-1";
                    while(!receiveMsg){
                        synchronized(Queue.get(j)){
                            if(!Queue.get(j).isEmpty()){
                                getValue = Queue.get(j).get(0);
                                Queue.get(j).remove(0);
                                receiveMsg = true ;
                            }
                        }

                    }
                    if(getValue == "1"){
                        s1 = s1 + w.get(j);
                    }
                    if(getValue == "0"){
                        s0 = s0 + w.get(j);
                    }
                }
            }


		     /*update value*/
            if(s0 >= 2.0/3.0){
                V = "0";
            }
            else if(s1 >= 2.0/3.0) {
                V = "1";
            }
            else V = undecided;

/*second phase*/
            s0 = 0.0;
            s1 = 0.0;
            su = 0.0;
				 /*send message to all other process except myself*/
            if(w.get(myId) > 0){
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"V", V);
                }
            }
				 /*receive message*/
            Queue.get(myId).add(V);
            for(int j= 0; j<numProc; j++){
                if(w.get(j)>0){
                    boolean receiveMsg = false;
                    String getValue = "-1";
                    while(!receiveMsg){
                        synchronized(Queue.get(j)){
                            if(!Queue.get(j).isEmpty()){
                                getValue = Queue.get(j).get(0);
                                Queue.get(j).remove(0);
                                receiveMsg = true ;
                            }
                        }
                    }
                    if(getValue == "1"){
                        s1 = s1 + w.get(j);
                    }
                    if(getValue == "0"){
                        s0 = s0 + w.get(j);
                    }
                    else su = su + w.get(j);
                }
            }

				/*update value*/
            if(s0 > 1.0/3.0){
                V = "0";
                myWeight = s0;
            }
            else if(s1 > 1.0/3.0) {
                V = "1";
                myWeight = s1;
            }
            else if(su > 1.0/3.0) {
                V = undecided;
                myWeight = su;
            }
 /*third phase*/

            if(i == myId){
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"KingValue", V);
                }
            }
            else{
                boolean receiveMsg = false;
                String kingValue = "-1";
                while(!receiveMsg){
                    synchronized(Queue.get(i)){
                        if(!Queue.get(i).isEmpty()){
                            kingValue = Queue.get(i).get(0);
                            Queue.get(i).remove(0);
                            receiveMsg = true ;
                        }
                    }

                }

                if((V == undecided) || (myWeight < (2.0/3.0)) ){
                    if(kingValue == undecided) V = "1";
                    else V = kingValue;
                }
            }
        }

        System.out.println("Final V = "+V);
    }
}




