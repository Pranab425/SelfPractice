package test2;

public class Interview2 {
    public static void main(String[] args) {
        //getPattern(5);
        checkPallindrome("aba");
    }
    public static void checkPallindrome(String input){
        boolean isPallindrome = true;
        for(int i = 0; i < input.length()/2; i++){
            if(input.charAt(i) != input.charAt(input.length()-1-i)){
                isPallindrome = false;
                break;
            }
        }
        if(isPallindrome){
            System.out.println("String is pallindrome");
        }
        else {
            System.out.println("String is not pallindrome");
        }
    }
    public static void getPattern(int i){
        for(int j = 1; j <= i; j++){
            int num = 1;
            int temp = j;
            while(temp != 0){
                System.out.print(num + "");
                num++;
                temp--;
            }
            System.out.println();
        }
    }
}
