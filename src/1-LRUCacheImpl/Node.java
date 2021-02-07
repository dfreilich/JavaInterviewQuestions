package interview1;

public class Node {
    public Node prev;
    public Node next;
    public String key;
    public int val;
    public Node(String key, int val) {
        this.key = key;
        this.val = val;
    }
    public String toString() { return "" + this.val; }
}
