package test;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Sample {
    public static void main(String[] args) {
        //String a = "HEllo#elseif" + "nfjrn" +"jfrinein";
        //a.replaceAll("\\\"\\+\\\"", "");
        //System.out.println(a);
        final Random rv = new Random(123);
        final Random rc = new Random(123);
        System.out.println(rv.nextInt() + " " + rc.nextInt());
        Date date = new Date();
        String itemId = date.getTime() + String.valueOf(UUID.randomUUID().toString().hashCode())
                .replaceAll("-", "");
        System.out.println("Hello -> " + date.getTime());
        System.out.println("Bello -> " + itemId);
        String randomBigInteger = String.valueOf(rv.nextInt());
        int randomBigInteger2 = rv.nextInt();
        //int hc = (uuidStr + randomBigInteger2 + date.getTime()).hashCode();
        //System.out.println("hashcode" + (uuidStr + randomBigInteger2 + date.getTime()).hashCode());
        if(randomBigInteger2 < 0){
            randomBigInteger2 *= -1;
        }
        String uuidStr = "";
        System.out.println(uuidStr);
        System.out.println(randomBigInteger);
        System.out.println(randomBigInteger2);
        String en = uuidStr+randomBigInteger + randomBigInteger2;
        System.out.println(en.hashCode());
    }
}
