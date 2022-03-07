package test;

public class LinkedList {
    Node head;
    static class Node {
        int data;
        Node next;
        Node(int d) {
            data = d;
            next = null;
        }
    }

    public void insert(LinkedList list, int data) {
        Node new_node = new Node(data);
        new_node.next = null;
        if (list.head == null) {
            list.head = new_node;
        } else {
            Node last = list.head;
            while (last.next != null) {
                last = last.next;
            }
            last.next = new_node;
        }
    }

    public static void printList(LinkedList list) {
        Node currNode = list.head;
        System.out.print("LinkedList: ");
        while (currNode != null) {
            System.out.print(currNode.data + " ");
            currNode = currNode.next;
        }
    }

    public static LinkedList reverse(LinkedList list) {
        Node node = list.head;
        Node previous = null, current;
        while (node != null) {
            current = node;
            node = node.next;
            current.next = previous;
            previous = current;
            list.head = current;
        }
        return list;
    }

    //1 -> 2 -> 3 -> 4 -> 5 -> 6
    //head = head.next;
    //ptr = ptr.next;
    //ptr.next = current;
    //current.next = null;
    //current = ptr;




















}