package WeightedByzantineAgreement.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class KingWBAP implements Runnable  {
    private int numProc ;
    private final String undecided = "-1";
    private int myId;
    private volatile String V;
    private volatile double[] w;
    private volatile int anchor;
    protected ArrayList<ArrayList<String>> Queue;
    private boolean type;

    public KingWBAP(int ID, int Process){
        this.myId = ID;
        this.numProc = Process;
        this.w = new double[numProc];
        this.Queue = new ArrayList<ArrayList<String>>(numProc);
        for(int i = 0;i<numProc;i++){
            Queue.add(new ArrayList<String>());
        }

        Random rand = new Random();
        this.V = Integer.toString(rand.nextInt(2));

        this.assignW();
        this.anchor = assignAnchor();

    }

    public void assignW(){
        for(int i = 0; i< numProc; i++){
            w[i]= (double)1/(double)numProc;
        }
    }

    public int assignAnchor(){
        double bound = 0.0;
        int Anchor = 0;
        double[] tmp_w =w.clone() ;
        Arrays.sort(tmp_w);
        for(int i = numProc-1; i > -1 ; i--){
            bound = bound + tmp_w[i];
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
        if(rcvMsg.retTag().equals("V") || rcvMsg.retTag().equals("KingValue")){
            synchronized(Queue.get(rcvMsg.retSrcId())){
                Queue.get(rcvMsg.retSrcId()).add(rcvMsg.retInfo());
            }

        }
        else if(rcvMsg.retTag().equals("start")){
            start();
        }
        else if(rcvMsg.retTag().equals("type")){
            this.type = Boolean.parseBoolean(rcvMsg.retInfo());
        }
        else if(rcvMsg.retTag().equals("weight")){
            String weight = rcvMsg.retInfo();
            weight = weight.substring(0,weight.length()-1);
            int count = 0;
            for (String tmp:weight.split(",")){
                this.w[count] = Double.parseDouble(tmp);
                count++;
            }

        }

    }

    private void decide(String value){
        System.out.println(myId+" decide sent!!!");
        WBA_instance.dOutTester.println(Integer.toString(myId)+" "+"decide"+" "+value);
        WBA_instance.dOutTester.flush();
    }

    public void start(){
        System.out.println("start agreement "+myId);
        new Thread(this).start();
    }
    public void run(){
        if(type){
        for(int i = 0; i<anchor; i++){
            double s0 = 0.0, s1 =0.0, su = 0.0;
            double myWeight = w[myId];   //initialize w[myId];
            MessageTrans trans = new MessageTrans(myId,numProc);
/*first phase*/
            if(w[myId] > 0){
				 /*send message to all other process except myself*/
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j, "V", V);
                }
            }
		     /*receive message*/
            Queue.get(myId).add(V);
            for(int j= 0; j<numProc; j++){
                if(w[j]>0){
                    boolean receiveMsg = false;
                    String getValue =Integer.toString(-1) ;
                    while(!receiveMsg){
                        synchronized(Queue.get(j)){
                            if(!Queue.get(j).isEmpty()){
                                getValue = Queue.get(j).get(0);
                                Queue.get(j).remove(0);
                                receiveMsg = true ;
                            }
                        }

                    }
                    if(getValue.equals(Integer.toString(1))){
                        s1 = s1 + w[j];
                    }
                    if(getValue.equals(Integer.toString(0))){
                        s0 = s0 + w[j];
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
            if(w[myId] > 0){
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"V", V);
                }
            }
				 /*receive message*/
            Queue.get(myId).add(V);
            for(int j= 0; j<numProc; j++){
                if(w[j]>0){
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
                    if(getValue.equals("1") ){
                        s1 = s1 + w[j];
                    }
                    if(getValue.equals("0")){
                        s0 = s0 + w[j];
                    }
                    else su = su + w[j];
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
                String kingValue = Integer.toString(-1);
                while(!receiveMsg){
                    synchronized(Queue.get(i)){
                        if(!Queue.get(i).isEmpty()){
                            kingValue = Queue.get(i).get(0);
                            Queue.get(i).remove(0);
                            receiveMsg = true ;
                        }
                    }

                }

                if((V.equals(undecided)) || (myWeight < (2.0/3.0)) ){
                    if(kingValue.equals(undecided)) V = "1";
                    else V = kingValue;
                }
            }
        }

        System.out.println("Final V = "+V);

        decide(V);
        }
        else
        {
            int i = (int)Math.random()*2;
            falty_process(i);

        }

    }

    private static void falty_process(int i){

    }


}
