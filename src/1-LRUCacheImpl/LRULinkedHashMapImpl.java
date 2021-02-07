package interview1;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRULinkedHashMapImpl extends LinkedHashMap<String, Integer> implements LRU {
    private int capacity;

    public LRULinkedHashMapImpl(int capacity)  {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    public int get(String key) {
        return super.getOrDefault(key, -1);
    }

    public int put(String key, int value) {
        super.put(key, value);
        return value;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
        return size() > this.capacity;
    }

    public String toString() {
        return super.toString();
    }
}
