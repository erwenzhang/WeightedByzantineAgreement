package WeightedByzantineAgreement.algorithm;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.Random;


public class Queen_WBA implements Runnable {
    private int numProc;
  //  private final byte undecided = -1;
    private int myId;
    private volatile String V;
    private volatile double[] w;
    private volatile int anchor;
    protected ArrayList<ArrayList<String>> Queue;

    public Queen_WBA(int ID, int numProc) {
        //super(ID,numProc);
        this.myId = ID;
        this.numProc = numProc;
       this.w = new double[numProc];
        this.Queue = new ArrayList<ArrayList<String>>(numProc);
        for(int i = 0; i < numProc; ++i) {
            Queue.add(new ArrayList<String>());
        }
        Random rand = new Random();
        this.V = Integer.toString(rand.nextInt(2)) ;
        assignW();
        this.anchor = assignAnchor();
    }

    public void procMsg(String line){
        System.out.println(line);
        Messages rcvMsg = new Messages();
        rcvMsg.parseMsg(line);
        if(rcvMsg.retTag().equals("V")|| rcvMsg.retTag().equals("QueenValue")){
            synchronized(Queue.get(rcvMsg.retSrcId())){
                Queue.get(rcvMsg.retSrcId()).add(rcvMsg.retInfo());
            }

        }
        else if(rcvMsg.retTag().equals("start")){
           start();
        }


    }

    public int assignAnchor(){
        double bound = 0.0;
        int Anchor = 0;
        double[] tmp_w = w.clone();
        Arrays.sort(tmp_w);
        for(int i = numProc-1; i > -1 ; i--){
            bound = bound + tmp_w[i];
            if(bound > 1.0/4.0){
                Anchor= numProc - i;
                break;
            }
        }
        return Anchor;
    }

    public void assignW(){
        for(int i = 0; i< numProc; i++){
            w[i]= ((double)1/(double)numProc);
        }
    }

    public void start(){
        System.out.println("start agreement " + myId);
        new Thread(this).start();
    }

    private void decide(String value){
        System.out.println(myId+" decide sent!!!");
       WBA_instance.dOutTester.println(Integer.toString(myId)+" "+"decide"+" "+value);
        WBA_instance.dOutTester.flush();
    }


    public void run(){
        for(int i = 0; i<anchor; i++){
            double s0 = 0.0, s1 =0.0;
            double myWeight;
            String myValue;
            MessageTrans trans = new MessageTrans(myId,numProc);
/*first phase*/
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
                }

            }

	      /*update value*/
            if(s1 >= 1.0/2.0){
                myValue = "1";
                myWeight = s1;
            }
            else {
                myValue = "0";
                myWeight = s0;
            }

            System.out.println(myId+" my value "+myValue+" my weight "+myWeight);

/*second phase*/
            String QueenValue = "-1";
            if(i == myId){
                V = myValue;
                for(int j = 0; j < numProc; j++){
                    trans.sendMessages(j,"QueenValue", V);
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

             //   System.out.println(myId+" my value "+ V+" phase 2");
            }
            System.out.println(myId+ " Final V = "+V + " anchor= "+ i);
        }
        decide(V);
    }

}
