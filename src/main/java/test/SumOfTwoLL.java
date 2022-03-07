package test;

public class SumOfTwoLL {
    public static void main(String[] args) {
        LinkedList linkedList1 = new LinkedList();
        linkedList1.insert(linkedList1, 3);
        linkedList1.insert(linkedList1, 1);
        linkedList1.insert(linkedList1, 4);

        LinkedList linkedList2 = new LinkedList();
        linkedList2.insert(linkedList2, 2);
        linkedList2.insert(linkedList2, 3);
        linkedList2.insert(linkedList2, 4);

        System.out.print("First ");
        LinkedList.printList(linkedList1);
        System.out.print("\nSecond ");
        LinkedList.printList(linkedList2);
        System.out.println("\n*******************************");

        Stack stack1 = new Stack();
        Stack stack2 = new Stack();

        LinkedList.Node currNode1 = linkedList1.head;
        LinkedList.Node currNode2 = linkedList2.head;

        while (currNode1 != null) {
            stack1.push(currNode1.data);
            currNode1 = currNode1.next;
        }

        while (currNode2 != null) {
            stack2.push(currNode2.data);
            currNode2 = currNode2.next;
        }

        LinkedList outputLinkedList = new LinkedList();
        int carryForward = 0;
        while (!stack1.isEmpty()) {
            int sum = stack1.pop() + stack2.pop();
            if (carryForward > 0) {
                sum = sum + carryForward;
            }
            if (sum%10 != 0) {
                outputLinkedList.insert(outputLinkedList, sum%10);
                carryForward = sum/10;
            }
            else {
                outputLinkedList.insert(outputLinkedList, sum);
                carryForward = 0;
            }
        }
        System.out.print("Output ");
        LinkedList.printList(LinkedList.reverse(outputLinkedList));

        int hex = Integer.parseInt("3008", 16);
        System.out.println("Hex " + hex);
    }
}