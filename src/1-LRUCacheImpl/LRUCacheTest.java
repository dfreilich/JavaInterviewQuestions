package interview1;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {
    LRU cache;

    @org.junit.jupiter.api.Nested
    class testLRUCache {
        @org.junit.jupiter.api.BeforeEach
        void setUp() {
            cache = new LRUCache(3);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.put("c", 3);
        }

        @org.junit.jupiter.api.Test
        void bumpOverCapacity() {
            cache.put("d", 4);
            assertEquals(-1, cache.get("a"));
        }

        @org.junit.jupiter.api.Test
        void getBringsToTop(){
            cache.get("a");
            cache.put("d", 4);
            assertEquals(-1, cache.get("b"));
            assertEquals(1, cache.get("a"));
        }
    }

    @org.junit.jupiter.api.Nested
    class testLRULinkedHashMap {
        @org.junit.jupiter.api.BeforeEach
        void setUp() {
            cache = new LRULinkedHashMapImpl(3);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.put("c", 3);
        }

        @org.junit.jupiter.api.Test
        void bumpOverCapacity() {
            cache.put("d", 4);
            assertEquals(-1, cache.get("a"));
        }

        @org.junit.jupiter.api.Test
        void getBringsToTop(){
            cache.get("a");
            cache.put("d", 4);
            assertEquals(-1, cache.get("b"));
            assertEquals(1, cache.get("a"));
        }
    }

}