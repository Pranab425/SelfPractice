package test2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class Interview4 {
    public static void main(String[] args) {
        int[] arr = {14, 46, 36,47,100, 94, 94, 52, 86,100, 36, 94, 89};
        int nthHeighest = 4;
        int result = Interview4.getHeighest(arr, nthHeighest);
        System.out.println(result);
        Interview4.getElements(arr);
    }
    public static int getHeighest(int[] arr, int nthHeighest){
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        int xheighest = 0;
        for(int i = 0; i < arr.length; i++){
            if(xheighest < arr[i]){
                stack1.push(arr[i]);
            } else {
                stack2.push(arr[i]);
            }
            xheighest = arr[i];
        }
        while((nthHeighest--) != 0){
            stack2.pop();
        }
        return stack2.peek();
    }
    public static void getElements(int[] arr){
        HashMap<Integer, Integer> hm = new HashMap<>();
        for(int i = 0; i < arr.length; i++){
            if(hm.containsKey(arr[i])){
                hm.put(arr[i], hm.get(arr[i]) + 1);
            }
            else {
                hm.put(arr[i], 1);
            }
        }
        Iterator itr = hm.keySet().iterator();
        while (itr.hasNext()){
            System.out.println(itr.next() + ", " + itr.next());
        }
    }
}
