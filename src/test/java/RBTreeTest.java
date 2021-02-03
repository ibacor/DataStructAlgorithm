import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RBTreeTest {
    private static int datas[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private static int pre[] = {30, 10, 0, 20, 50, 40, 70, 60, 90, 80, 100};
    private static int in[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

    @Test
    void get() {
        RBTree tree = new RBTree();
        for (int data: datas) {
            tree.put(data);
        }
        assertNull(tree.get(5));
        assertEquals(10, tree.get(10).key);
        assertEquals(100, tree.get(100).key);
    }

    @Test
    void put() {
        RBTree tree = new RBTree();
        for (int data: datas) {
            tree.put(data);
            assertTrue(checkRBTree(tree.root));
        }
        LinkedList<Integer> preList = new LinkedList<>();
        preOrder(tree.root, preList);
        assertArrayEquals(pre, listToArray(preList));

        LinkedList<Integer> inList = new LinkedList<>();
        inOrder(tree.root, inList);
        assertArrayEquals(in, listToArray(inList));
    }

    @Test
    void remove() {
        RBTree tree = new RBTree();
        for (int data: datas) {
            tree.put(data);
        }

        // 删除黑色节点，有两个黑子节点
        tree.remove(10);
        LinkedList<Integer> preList = new LinkedList<>();
        preOrder(tree.root, preList);
        assertArrayEquals(new int[]{50,30,20,0,40,70,60,90,80,100}, listToArray(preList));
        LinkedList<Integer> inList = new LinkedList<>();
        inOrder(tree.root, inList);
        assertArrayEquals(new int[]{0,20,30,40,50,60,70,80,90,100}, listToArray(inList));

        // 删除红色节点，没有子节点
        tree.remove(0);
        preList = new LinkedList<>();
        preOrder(tree.root, preList);
        assertArrayEquals(new int[]{50,30,20,40,70,60,90,80,100}, listToArray(preList));
        inList = new LinkedList<>();
        inOrder(tree.root, inList);
        assertArrayEquals(new int[]{20,30,40,50,60,70,80,90,100}, listToArray(inList));

        // 删除根节点，有两个黑子节点
        tree.remove(50);
        preList = new LinkedList<>();
        preOrder(tree.root, preList);
        assertArrayEquals(new int[]{60,30,20,40,90,70,80,100}, listToArray(preList));
        inList = new LinkedList<>();
        inOrder(tree.root, inList);
        assertArrayEquals(new int[]{20,30,40,60,70,80,90,100}, listToArray(inList));

        tree = new RBTree();
        for (int data: datas) {
            tree.put(data);
        }
        assertTrue(checkRBTree(tree.root));
        for (int data: datas) {
            tree.remove(data);
            assertTrue(checkRBTree(tree.root));
        }
    }

    /**
     * 检测符合红黑树定义，根据3个标准判断：
     * 1 根节点是否是黑色
     * 2 红色节点的子节点是否是黑色
     * 3 每个节点到任意叶子节点的黑色节点数是否相同
     */
    boolean checkRBTree(RBTree.RBTreeNode node) {
        if (RBTree.colorOf(node) == RBTree.RED) {
            return false;
        }

        return check(node);
    }

    private boolean check(RBTree.RBTreeNode node) {
        if (node == null) {
            return true;
        }

        if (RBTree.colorOf(node) == RBTree.RED) {
            return RBTree.colorOf(node.left) == RBTree.BLACK && RBTree.colorOf(node.right) == RBTree.BLACK;
        }

        // 使用左子树的黑节点数作为基准
        int count = 0;
        int num = 0;
        RBTree.RBTreeNode p = node;
        while (p != null) {
            if (p.color == RBTree.BLACK) {
                count++;
            }
            p = p.left;
        }
        if (!checkBlackCount(node, count, num)) {
            return false;
        }

        return check(node.left) && check(node.right);
    }

    private boolean checkBlackCount(RBTree.RBTreeNode p, int count, int num) {
        if (p == null) {
            return num == count;
        }

        if (p.color == RBTree.BLACK) {
            num++;
        }
        return checkBlackCount(p.left, count, num) && checkBlackCount(p.right, count, num);
    }

    void preOrder(RBTree.RBTreeNode p, LinkedList<Integer> list) {
        if (p == null) {
            return;
        }

        list.add(p.key);
        preOrder(p.left, list);
        preOrder(p.right, list);
    }

    void inOrder(RBTree.RBTreeNode p, LinkedList<Integer> list) {
        if (p == null) {
            return;
        }

        inOrder(p.left, list);
        list.add(p.key);
        inOrder(p.right, list);
    }

    int[] listToArray(List<Integer> list) {
        int[] a = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }
}