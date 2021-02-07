package interview2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestMap {
    public static void main(String[] args) {
        Map<String, List<String>> testMap = new ConcurrentHashMap<>();
        testMap.put("a", new ArrayList<>());
        testMap.get("a").add("b");
        System.out.println(testMap.toString());
    }
}
