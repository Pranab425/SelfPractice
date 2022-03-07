package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CodeObfuscate {
    public static void main(String[] args) throws IOException {
        String inputFilePath = args[0];
        obfuscate(inputFilePath);
    }

    public static void obfuscate(String path) throws IOException {
        boolean multipleLinesLogs = false;
        String line;
        StringBuffer sb = new StringBuffer();
        String newFileName = "obfuscated.c";
        boolean multiLineComment = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))))) {
            while ((line = reader.readLine()) != null){
                line = line.trim();
                if(line.isEmpty()) continue;
                if(line.startsWith("/**")) {
                    multiLineComment = true;
                    continue;
                }
                if (line.startsWith("*/") && multiLineComment == true){
                    multiLineComment = false;
                    continue;
                }
                if (multiLineComment == true) continue;

                if(line.startsWith("/*") && line.endsWith("*/")) continue;

                if(line.startsWith("#")) {
                    sb.append("\n");
                    sb.append(line);
                    sb.append("\n");
                    continue;
                }
                if(multipleLinesLogs){
                    if(sb.toString().endsWith("+")){
                        sb.deleteCharAt(sb.length()-1);
                        if(sb.toString().trim().endsWith("\"")) {
                            String temp = sb.substring(0, sb.length()-2);
                            sb.delete(0, sb.length());
                            sb.append(temp);
                            if(line.startsWith("\"")){
                                line = line.substring(1);
                            }
                        }
                    }
                    if(line.startsWith("+")) {
                        line = line.substring(1);
                        if(line.startsWith("\"")){
                            line = line.substring(1);
                        }
                    }

                    if(line.endsWith(";")) multipleLinesLogs = false;

                }
                if(line.contains("Log") || line.contains("print")) {
                    if(!line.endsWith(";")) multipleLinesLogs = true;
                    sb.append(line);
                    continue;
                }
                sb.append(line);
                if(line.equals("else") || line.endsWith("else")) {
                    sb.append(" ");
                }
            }
            System.out.println("Obfuscated code is -> ");
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage() + e);
        }
        Files.write(Paths.get(newFileName), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND);
    }
}
