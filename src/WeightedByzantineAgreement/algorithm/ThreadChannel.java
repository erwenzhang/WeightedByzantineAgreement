package WeightedByzantineAgreement.algorithm;

import java.net.SocketTimeoutException;

/**
 * Created by WENWEN on 11/8/15.
 */
public class ThreadChannel extends Thread {
    int fromProcessId;
    Queen_WBA msg_agreement;

    public ThreadChannel(int fromProcessId, Queen_WBA msg_agreement){
        this.fromProcessId = fromProcessId;
        this.msg_agreement = msg_agreement;
    }

    public void run() {
        int count = 0;
        while(true){
            String str;
            count = count + 1;
            try{
                str = WBA_instance.dataIn[fromProcessId].readLine();
                msg_agreement.procMsg(str);

            }catch (SocketTimeoutException e)
            {
                e.printStackTrace();
                if(count%2 == 1)
                str = Integer.toString(fromProcessId) + " "+"V" +" "+"0";
                else
                str = Integer.toString(fromProcessId) + " "+"QueenValue" +" "+"0";
                msg_agreement.procMsg(str);

            }
            catch (Exception e1){
                e1.printStackTrace();
            }

        }




    }
}
