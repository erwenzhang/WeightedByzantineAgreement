package QueenWeightedByzantineAgreement;

/**
 * Created by apple on 11/8/15.
 */
public class ThreadProcess extends Thread{
    Queen_WBA msg_agreement;
    public ThreadProcess(Queen_WBA msg_agreement){
        this.msg_agreement = msg_agreement;

    }
    public void run(){
        msg_agreement.processing();
    }
}
