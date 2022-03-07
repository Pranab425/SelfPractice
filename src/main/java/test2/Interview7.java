package test2;

//Numb ={ 5,7,8,11, 23, 24, 25}

//Ans Numb ={ 5, 25, 8,24,7, 11.23}

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Interview7 {
    public static void main(String[] args) {
        int[] input = { 5,7,8,11, 23, 24, 25};
        TreeSet<Integer> fiveMultiple = new TreeSet<>();
        TreeSet<Integer> evenNum = new TreeSet<>();
        TreeSet<Integer> oddNum = new TreeSet<>();
        for(int i = 0 ; i < input.length; i++){
            if(input[i]%5 == 0){
                fiveMultiple.add(input[i]);
            }
            else if(input[i]%2 == 0){
                evenNum.add(input[i]);
            }
            else if(input[i]%2 != 0){
                oddNum.add(input[i]);
            }
        }
        List<Integer> ls = new ArrayList<>();
        Iterator<Integer> i = fiveMultiple.iterator();
        while (i.hasNext()){
            ls.add(i.next());
        }
        i = evenNum.iterator();
        while (i.hasNext()){
            ls.add(i.next());
        }
        i = oddNum.iterator();
        while (i.hasNext()){
            ls.add(i.next());
        }
        i = ls.iterator();
        while (i.hasNext()){
            System.out.print(" " + i.next());
        }
    }
}
