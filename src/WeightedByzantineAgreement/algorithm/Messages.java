package WeightedByzantineAgreement.algorithm;
import java.util.StringTokenizer;

public class Messages {
    private int  SourceId;
    private String tag;
    private String Info;


    public Messages(int source, String tag, String info){
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

    public String retInfo(){
        return Info;
    }

    public String formString(){
        String transMsg;
        transMsg = Integer.toString(SourceId)+" "+tag+" "+Info;
        return transMsg;
    }
    public void parseMsg(String line){
        StringTokenizer token = new StringTokenizer (line);
        this.SourceId = Integer.parseInt(token.nextToken());
        this.tag  = token.nextToken();
        this.Info = token.nextToken();
    }

}
