package test2;

import java.util.HashSet;

class A{
    void run(){
        System.out.println("Inside A run()");
    }
    void walk(){
        System.out.println("Inside A walk()");
    }
}
class B extends A {
    void run() {
        System.out.println("Iside ff");
    }
}
public class Interview5 extends A{
    void run() {
        System.out.println("Inside Interview5 run()");
    }
    void walk(){
        super.walk();
        System.out.println("Inside Interview5 walk()");
    }
    public static void main(String[] args) {
        A a = new Interview5();
        a.run();
        HashSet<Object> hs = new HashSet<>();
        hs.add(a);
        hs.add("ef");
    }
}
