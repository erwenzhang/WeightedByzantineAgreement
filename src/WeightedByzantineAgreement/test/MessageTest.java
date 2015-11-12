package WeightedByzantineAgreement.test;
import java.util.StringTokenizer;

import java.util.StringTokenizer;

public class MessageTest {
    private int  SourceId;
    private String tag;
    private Byte Info;


    public MessageTest(int source, String tagIn, Byte info){
        SourceId = source;
        tag = tagIn;
        Info = info;
    }
    public MessageTest() {
        // TODO Auto-generated constructor stub
    }
    public int retSrcId(){
        return SourceId;
    }

    public String retTag(){
        return tag;
    }

    public Byte retInfo(){
        return Info;
    }

    public String formString(){
        String transMsg;
        transMsg = Integer.toString(SourceId)+" "+tag+" "+Integer.toString(Info);
        return transMsg;
    }
    public void parseMsg(String line){
        StringTokenizer token = new StringTokenizer (line);
        SourceId = Integer.parseInt(token.nextToken());
        tag  = token.nextToken();
        Info = (byte)Integer.parseInt(token.nextToken());
    }

    public MessageTest formMsg(){
        return new MessageTest(SourceId,tag,Info);
    }


}

