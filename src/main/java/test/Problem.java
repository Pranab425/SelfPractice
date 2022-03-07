package test;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    public static void main(String[] args) {

        List<String> inputByLine = new ArrayList<String>();
        int totalTC = Integer.parseInt(inputByLine.get(0));
        int count = 1;
        for(int tc = 0; tc < totalTC; tc++){
            String[] arr = inputByLine.get(count).split(" ");
            int[] arr2 = new int[arr.length];
            for(int c = 0 ; c < arr.length; c++){
                arr2[c] = Integer.parseInt(arr[c]);
            }
            String[] arr3 = inputByLine.get(count+1).split(" ");
            int[] arr4 = new int[arr3.length];
            for(int c = 0 ; c < arr3.length; c++){
                arr4[c] = Integer.parseInt(arr3[c]);
            }
            count = count + 2;
            boolean notFound = false;
            for(int i = 0; i < arr2[0]; i++){
                int temp = arr4[i];
                while(temp > Integer.parseInt(arr[1])){
                    if (hasNumber(temp, Integer.parseInt(arr[1]))) {
                        notFound = false;
                        break;
                    }
                    temp -= Integer.parseInt(arr[1]);
                    notFound = true;
                }
                if(notFound == true){
                    System.out.println("NO");
                }
                else System.out.println("YES");
            }
        }
    }
    public static boolean hasNumber(int number, int actualNumber)
    {
        while(number > 0)
        {
            if(number % 10 == actualNumber)
                return true;

            number=number/10;
        }
        return false;
    }
}
