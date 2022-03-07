package test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestDecode {
    public static void main(String[] args) {
        CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
        String line = "��Inbox|298|group upload|Request for \\u00001Clarification|1|BL|IBL22965,IBL48668";
        byte[] inputBytes = line.getBytes();
        try{
            String s = new String(inputBytes, "UTF-8");
            charsetDecoder.decode(ByteBuffer.wrap(inputBytes));
            System.out.println("Success");
        } catch (CharacterCodingException | UnsupportedEncodingException cce){
            System.out.println(cce);
        }


        String[] arr = {"1", "-7"};
        Arrays.sort(arr);
        System.out.println(arr[0] + " " + arr[1]);
    }
}
