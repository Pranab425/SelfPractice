package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        //String commandOutput;
        //String UUIDstring;
        String OS = System.getProperty("os.name").toLowerCase();
        //Process p;
        String command = null;
        if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
            command = "cat /etc/fstab";
            //command = "blkid";
        }
        else if (OS.indexOf("sunos") >= 0) {
            command = "virtinfo -u";
        }
        try {
            String commandOutput;
            String UUIDstring;
            Process p = Runtime.getRuntime().exec("cat /etc/fstab");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((commandOutput = br.readLine()) != null) {
                if (commandOutput.contains("UUID")) {
                    String UUIDpart[] = commandOutput.split(" ");
                    int outputLength = UUIDpart.length;
                    while (outputLength != 0) {
                        if (UUIDpart[outputLength-1].contains("UUID")) {
                            UUIDstring = UUIDpart[outputLength-1].split("=")[1].trim();
                            if (UUIDstring.length() == 36) {
                                System.out.println("UUID: " + UUIDstring);
                            }
                        }
                        outputLength--;
                    }
                }
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {}
    }
}
