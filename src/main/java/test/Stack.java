package test;

public class Stack {
    static final int MAX = 10;
    int top;
    int a[] = new int[MAX];

    boolean isEmpty() {
        return (top < 0);
    }

    Stack() {
        top = -1;
    }

    boolean push(int x) {
        if (top >= (MAX-1)) {
            return false;
        }
        else {
            a[++top] = x;
            return true;
        }
    }

    int pop() {
        if (top < 0) {
            return 0;
        }
        else {
            int x = a[top--];
            return x;
        }
    }
}
