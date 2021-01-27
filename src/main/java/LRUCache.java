import java.util.HashMap;

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 *
 * LRUCache(int capacity) 以正整数作为容量capacity 初始化 LRU 缓存
 * int get(int key) 如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1 。
 * void put(int key, int value)如果关键字已经存在，则变更其数据值；如果关键字不存在，则插入该组「关键字-值」。当缓存容量达到上限时，它应该在写入新数据之前删除最久未使用的数据值，从而为新的数据值留出空间。
 *
 * 进阶：你是否可以在O(1) 时间复杂度内完成这两种操作？
 */
public class LRUCache {
    /**
     * 哈希表的增删改查时间复杂度是O(1)，链表可表征时序关系，
     * 两者结合成 哈希链表 的数据结构，即可复合要求
     */
    private HashMap<Integer, DLinkedNode> map;
    private DLinkedNodeList list;
    private int cap;

    public LRUCache(int capacity) {
        cap = capacity;
        map = new HashMap<>();
        list = new DLinkedNodeList();
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            DLinkedNode node = map.get(key);
            // 删除节点
            list.remove(node);
            // 重新添加到头部
            list.addFirst(node);
            return node.value;
        }
        return -1;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            DLinkedNode node = map.get(key);
            node.value = value;
            list.remove(node);
            list.addFirst(node);
        } else {
            DLinkedNode node = new DLinkedNode(key, value);
            // 检查容量是否已满
            if (list.size() >= cap) {
                DLinkedNode lastNode = list.removeLast();
                map.remove(lastNode.key);
            }
            list.addFirst(node);
            map.put(key, node);
        }
    }

    static class DLinkedNode {
        int key;
        int value;
        DLinkedNode pre;
        DLinkedNode next;

        public DLinkedNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 双向链表。
     * 使用双向链表是因为链表删除某个节点需要知道前驱和后驱节点
     */
    static class DLinkedNodeList {
        private int size = 0;
        private DLinkedNode dummyHead;
        private DLinkedNode dummyTail;

        public DLinkedNodeList() {
            dummyHead = new DLinkedNode(-1, -1);
            dummyTail = new DLinkedNode(-1, -1);
            dummyHead.next = dummyTail;
            dummyTail.pre = dummyHead;
        }

        void addFirst(DLinkedNode node) {
            node.next = dummyHead.next;
            node.pre = dummyHead;
            dummyHead.next.pre = node;
            dummyHead.next = node;
            size++;
        }

        void remove(DLinkedNode node) {
            node.pre.next = node.next;
            node.next.pre = node.pre;
            size--;
        }

        DLinkedNode removeLast() {
            DLinkedNode lastNode = dummyTail.pre;
            lastNode.pre.next = dummyTail;
            dummyTail.pre = lastNode.pre;
            size--;
            return lastNode;
        }

        int size() {
            return size;
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1,1);
        cache.put(2,2);
        cache.get(1);
        cache.put(3,3);
        cache.get(2);
        cache.put(4,4);
        cache.get(1);
        cache.get(3);
        cache.get(4);
    }
}
