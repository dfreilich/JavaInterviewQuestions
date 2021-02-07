package interview1;

public class LinkedNodeList {
    private Node head, tail;
    private int size;

    public LinkedNodeList(){
        head = new Node("", Integer.MAX_VALUE);
        tail = new Node("", Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    public void add(Node curr) {
        curr.prev = head;
        curr.next = head.next;
        curr.next.prev = curr;
        head.next = curr;
        size++;
    }

    public void makeHead(Node curr) {
        if(curr.next != null && curr.prev != null) {
            remove(curr);
        }

        add(curr);
    }

    public Node remove(Node curr) {
        if (curr == null || curr == head || curr == tail) return null;

        curr.prev.next = curr.next;
        curr.next.prev = curr.prev;
        size--;
        return curr;
    }

    public Node removeTail() {
        if(tail.prev == head) return tail; //Empty list
        return remove(tail.prev);
    }

    public int size() {
        return this.size;
    }
}
