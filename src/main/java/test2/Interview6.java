package test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interview6 {
    public static void main(String[] args) {
        List<Integer> listOfIntegers = new ArrayList<>();
        listOfIntegers.addAll(Arrays.asList(1,4,2,7,5));

        System.out.println(listOfIntegers.stream().filter(i -> (i%2 == 0)).mapToInt(i -> i*i).sum());
    }
}
