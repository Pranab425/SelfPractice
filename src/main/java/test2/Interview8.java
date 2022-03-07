package test2;

import java.util.HashMap;
import java.util.Map;

public class Interview8 {
    public static void main(String[] args) {
        String str[] = { "168.0.0.1 is the first ip address","168.0.0.2 is the second ip address","168.0.0.1 is the first ip address" };
        HashMap<String, Integer> hashMap = new HashMap<>();
        for(int i = 0; i < str.length; i++){
            String temp = str[i].split(" ")[0];
            if(hashMap.containsKey(temp)){
                hashMap.put(temp, hashMap.get(temp)+1);
            }
            else hashMap.put(temp, 1);
        }
        String maxValue = "";
        int maxCount = 0;
        for(Map.Entry<String, Integer> map : hashMap.entrySet()){
            if(map.getValue() > maxCount){
                maxValue = map.getKey();
                maxCount = map.getValue();
            }
        }

        System.out.println(maxValue);

        int intArray[]= {1,2,3,4,5,6,8,9,10};
        int tempSum = 0;
        for(int i = 0; i < intArray.length; i++){
            tempSum += intArray[i];
        }
        int MaxSum = (intArray[intArray.length-1]*(intArray[intArray.length-1]+1))/2;
        System.out.println(MaxSum-tempSum);
    }
}
