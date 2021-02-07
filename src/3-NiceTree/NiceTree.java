package interview1;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class NiceTree {
    class Node {
        Node right;
        Node left;
        String data;

        public Node(String data){
            this.data = data;
        }
    }

    // root is null
    // root.right && root.left == null
    // root.right && root.left != null, isNice(root.right) && isNice(root.left)
    /*

    /\

    /\
   /\ /\

    /\
   /\

   /\
    /\

    /\
   /\ \

     */
    boolean isNice(Node root) {
        //todo
        if (root == null) return true;

        Queue<Node> queue = new PriorityQueue<>();
        Node newLevel = new Node("new-level");
        queue.add(root);
        queue.add(newLevel);
        boolean children = false;
        boolean first = true;
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            if(current == newLevel) {
                children = false;
                first = true;
                if(!queue.isEmpty()) queue.add(newLevel);
            } else {
                if(first) {
                    children = current.left != null || current.right != null;
                    first = false;
                }

                if(children) {
                    if(current.left == null || current.right == null) return false;

                    queue.add(current.left);
                    queue.add(current.right);
                } else {
                    if(current.left != null || current.right != null) return false;
                }
            }
        }

        return true;
    }
}
