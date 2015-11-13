package WeightedByzantineAgreement.algorithm;

import java.net.SocketTimeoutException;

/**
 * Created by apple on 11/11/15.
 */
public class ToTesterChannel extends Thread {
    private int process_id;
    private Queen_WBA msg_agreement;
    public ToTesterChannel(int process_id,Queen_WBA msg_agreement){
        this.process_id = process_id;
        this.msg_agreement = msg_agreement;
    }
    public void run(){
        while(true){
            String str;
            try{
                System.out.println(" before subprocesses started ");
                str = WBA_instance.dInTester.readLine();
                msg_agreement.procMsg(str);



            }catch (SocketTimeoutException e)
            {
                e.printStackTrace();

            }
            catch (Exception e1){
                e1.printStackTrace();
            }

        }

    }
}
