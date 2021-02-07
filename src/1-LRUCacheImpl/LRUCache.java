package interview1;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache implements LRU {
    Map<String, Node> cache;
    LinkedNodeList list;
    int capacity;

    public LRUCache(int capacity)  {
        if (capacity <= 0)  throw new IllegalArgumentException();
        cache = new LinkedHashMap<>(capacity);
        list = new LinkedNodeList();
        this.capacity = capacity;
    }

    public int get(String key) {
        Node current = cache.get(key);
        if (current != null) {
            list.makeHead(current);
            return current.val;
        }
        return -1;
    }

    public int put(String key, int value) {
        Node curr = cache.getOrDefault(key, new Node(key, value));
        if (!cache.containsKey(key)) {
            if (list.size() == this.capacity) {
                Node remove = list.removeTail();
                cache.remove(remove.key);
            }
            cache.put(key, curr);
        }
        list.makeHead(curr);
        return curr.val;
    }
}

