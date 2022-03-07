package test2;

import java.util.Stack;

public class Interview3 {
    public static void main(String[] args) {
        //String input = "{{{";
        //String output = checkBalanced(input);
        int[] input = {4, 5, 15, 2, 2, 10, 8};
        int[] output = nextBigNumber(input);
        for(int k = 0; k < output.length; k++){
            System.out.print(output[k] + " ");
        }
    }
    //4, 5, 2, 25, 10
    public static int[] nextBigNumber(int[] input){
        int[] result = new int[input.length];
        int maxElement = input[input.length-1];
        int current = maxElement;
        result[input.length-1] = -1;
        for(int i = input.length-2; i >= 0; i--){
            if(input[i] > maxElement){
                maxElement = input[i];
                current = input[i];
                result[i] = -1;
            }
            if(input[i] < maxElement){
                if(input[i] < current){
                    result[i] = current;
                } else result[i] = maxElement;
                current = input[i];
            }
        }
        return result;
    }
    public static String checkBalanced(String input){
        String result = "Balanced";
        char[] charArray = input.toCharArray();
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < charArray.length; i++){
            if(charArray[i] == '[' || charArray[i] == '(' || charArray[i] == '{'){
                stack.push(charArray[i]);
            }
            if(charArray[i] == ']'){
                if(stack.peek() == '['){
                    stack.pop();
                }
            }
            if(charArray[i] == '}'){
                if(stack.peek() == '{'){
                    stack.pop();
                }
            }
            if(charArray[i] == ')'){
                if(stack.peek() == '('){
                    stack.pop();
                }
            }
        }
        if(!stack.isEmpty()){
            result = "Not Balanced";
            return result;
        }
        return result;
    }
}
