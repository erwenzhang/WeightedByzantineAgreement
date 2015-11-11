package WeightedByzantineAgreement.algorithm;
import java.util.StringTokenizer;

public class Messages {
    private int  SourceId;
    private String tag;
    private Byte Info;


    public Messages(int source, String tag, Byte info){
        this.SourceId = source;
        this.tag = tag;
        this.Info = info;
    }
    public Messages() {
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
        this.SourceId = Integer.parseInt(token.nextToken());
        this.tag  = token.nextToken();
        this.Info = (byte)Integer.parseInt(token.nextToken());
    }

}
