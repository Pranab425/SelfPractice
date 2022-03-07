package test;

import java.util.Scanner;

public class JSONParseTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter JSON string");
        String invalidJson = sc.nextLine();
        invalidJson.replaceAll("\"", "\\\"");
        System.out.println("String after replacing quotes");
        System.out.println(invalidJson);
    }
}
