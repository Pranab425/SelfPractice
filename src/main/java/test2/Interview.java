package test2;

//GOOD, 2 -> ODOG
//EARTH, 3 -> REHAT

public class Interview {
    public class Node{
        char data;
        Node next;
        public Node(char data) {
            this.data = data;
        }
    }
    public Node head = null;
    public Node tail = null;
    public void addInCircularLL(char input){
        Node newNode = new Node(input);
        if(head == null) {
            head = newNode;
            tail = newNode;
            newNode.next = head;
        }
        else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head;
        }
    }
    public void display(int itr) {
        Node current = head;
        int counter = 1;
        if(head == null) {
            System.out.println("List is empty");
        }
        else {
            System.out.println("Nodes of the circular linked list: ");
            do{
                if(counter%itr == 0){
                    System.out.print(" "+ current.data);
                }
                current = current.next;
                counter++;
            }while(current != head);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Interview interview = new Interview();
        String input = "EARTH";
        int itr = 3;
        for(int i = 0; i < input.length(); i++){
            interview.addInCircularLL(input.charAt(i));
        }
        interview.display(itr);


    }


    public void reArrange(String input, int itr, int[] statusArray){
        if(input == ""){
            return;
        }
        if(input.length() == 1){
            System.out.print(input);
            return;
        }
        char[] strArray = input.toCharArray();
        statusArray = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++){
            if((i+1)%itr == 0 && statusArray[i] != 1){
                System.out.print(strArray[i]);
                statusArray[i] = 1;
            }
        }
        StringBuffer sb2 = new StringBuffer();
        for(int j = 0; j < statusArray.length; j++){
            if(statusArray[j] != 1){
                sb2.append(strArray[j]);
            }
        }
        reArrange(sb2.toString(), itr, statusArray);
    }
}
