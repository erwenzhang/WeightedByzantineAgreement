/**
 * Created by apple on 11/13/15.
 */
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
public class ProcessBuilderDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("/Users/apple/Documents/myjava/test.bat", "ABC", "XYZ");
        processBuilder.directory(new File("/Users/apple/Documents/myjava/"));
        File log = new File("log.txt");
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(Redirect.appendTo(log));
        Process p = processBuilder.start();
        p.waitFor();
        System.out.println("Done");
    }
}