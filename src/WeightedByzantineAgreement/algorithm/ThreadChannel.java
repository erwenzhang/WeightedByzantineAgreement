package WeightedByzantineAgreement.algorithm;

import java.net.SocketTimeoutException;

/**
 * Created by WENWEN on 11/8/15.
 */
public class ThreadChannel extends Thread {
    int fromProcessId;
    Queen_WBA msg_agreement;
  //  KingWBAP msg_agreement;
    int hostId;

    public ThreadChannel(int hostId, int fromProcessId, Queen_WBA msg_agreement){
        this.fromProcessId = fromProcessId;
        this.msg_agreement = msg_agreement;
        this.hostId = hostId;
    }

    public void run() {
        int count = 0;
        System.out.println(Integer.toString(hostId)+" THREADly listen to process: "+Integer.toString(fromProcessId));
        while(true){
            String str;
            count = count + 1;
            try{

               // System.out.println(fromProcessId);
                str = WBA_instance.dataIn[fromProcessId].readLine();
              //  System.out.println(str);
                msg_agreement.procMsg(str);

            }catch (SocketTimeoutException e)
            {
               // System.out.println("wenwen "+count);
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
