/**
 * https://www.jianshu.com/p/e136ec79235c
 * https://zhuanlan.zhihu.com/p/22800206
 */
public class RBTree {
    /* 根节点 */
    RBTreeNode root;

    /**
     * 使用BST的性质
     */
    public RBTreeNode get(int key) {
        RBTreeNode p = root;
        while (p != null) {
            if (p.key == key) {
                return p;
            } else if (p.key < key) {
                p = p.right;
            } else {
                p = p.left;
            }
        }
        return null;
    }

    public void put(int key) {
        // 1.根节点为空，直接设为根节点
        if (root == null) {
            root = new RBTreeNode(key, BLACK);
        }

        // 查找插入位置
        RBTreeNode parent = findInsertNode(root, key);
        if (parent == null) {
            // 2.key已经存在，直接返回
            return;
        }

        RBTreeNode node = new RBTreeNode(key);
        node.parent = parent;
        if (key < parent.key) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        // 剩余3、4种情形
        balanceInsertion(node);
    }

    /**
     * 插入自平衡
     *
     * @param node 插入的节点
     */
    private void balanceInsertion(RBTreeNode node) {
        RBTreeNode parent = node.parent;
        if (parent == null) {
            // 4.1情形下，节点是根节点，重新设为黑色
            // 这是红黑树唯一增加黑色节点层数的情形
            node.color = BLACK;
            return;
        }

        if (parent.color == BLACK) {
            // 3.父节点是黑节点，直接插入
            if (parent.key < node.key) {
                parent.right = node;
            } else {
                parent.left = node;
            }
        } else {
            // 4.父节点是红节点，分3种情况
            RBTreeNode pp = parent.parent;
            RBTreeNode uncle;
            if (parent == pp.left) {
                uncle = pp.right;
            } else {
                uncle = pp.left;
            }

            if (uncle != null && uncle.color == RED) {
                // 4.1叔叔节点存在并且为红色
                parent.color = BLACK;
                uncle.color = BLACK;
                pp.color = RED;
                // 把pp设置为当前节点，自底向上自平衡
                balanceInsertion(pp);
            } else {
                if (parent.parent == pp.left) {
                    // 4.2插入节点的父节点是祖父节点的左节点
                    if (node.key < parent.key) {
                        // 4.2.1插入节点是父节点的左节点
                        parent.color = BLACK;
                        pp.color = RED;
                        rotateRight(pp);
                    } else {
                        // 4.2.2插入节点是父节点的右节点
                        rotateLeft(parent);
                        balanceInsertion(parent);
                    }
                } else {
                    // 4.3插入节点的父节点是祖父节点的右节点,和4.2对称
                    if (node.key < parent.key) {
                        // 4.3.2插入节点是父节点的左节点
                        rotateRight(parent);
                        balanceInsertion(parent);
                    } else {
                        // 4.3.1插入节点是父节点的右节点
                        parent.color = BLACK;
                        pp.color = RED;
                        rotateLeft(pp);
                    }
                }
            }
        }
    }

    /**
     * 查找插入位置
     *
     * @return 返回插入节点的父节点，如果已有则返回null
     */
    private RBTreeNode findInsertNode(RBTreeNode root, int key) {
        RBTreeNode node = root;
        RBTreeNode parent = node;
        while (node != null) {
            parent = node;
            if (node.key == key) {
                // 有相同的key，可更新value，目前返回null
                return null;
            } else if (node.key < key) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return parent;
    }

    public void remove(int key) {
        RBTreeNode node = get(key);
        if (node != null) {
            // 对于红黑树来说，可认为删除的是替换节点
            RBTreeNode replaceNode = findReplaceNode(node);
            balanceDeletion(replaceNode);

            if (replaceNode.parent == null) {
                // 删除的是根节点
                root = null;
            } else {
                if (replaceNode == replaceNode.parent.left) {
                    replaceNode.parent.left = null;
                } else {
                    replaceNode.parent.right = null;
                }
                replaceNode.parent = null;
            }
        }
    }

    /**
     * 删除自平衡，通过变色、左旋、右旋三种操作
     * 在这个方法里不做实际删除操作
     * @param node 删除节点
     */
    private void balanceDeletion(RBTreeNode node) {
        while (node != root && node.color == BLACK) {
            RBTreeNode p = node.parent;
            // 2 删除节点是黑色节点，分两种子情景
            // 因为该节点是黑色，所以兄弟节点必定存在
            RBTreeNode s;
            if (node == p.left) {
                // 2.1 是父节点的左节点
                s = p.right;
                if (colorOf(s) == RED) {
                    // 2.1.1 兄弟节点是红色节点(必定有两个黑子节点)
                    s.color = BLACK;
                    s.left.color = RED;
                    rotateLeft(p);
                    break;
                } else {
                    // 2.1.2 兄弟节点是黑色节点
                    if (colorOf(s.right) == RED) {
                        // 2.1.2.1 兄弟节点的右节点是红色节点
                        s.color = p.color;
                        p.color = BLACK;
                        s.right.color = BLACK;
                        rotateLeft(p);
                        break;
                    } else {
                        if (colorOf(s.left) == RED && colorOf(s.right) == BLACK) {
                            // 2.1.2.2 兄弟节点的右节点是黑色节点，左节点是红色节点
                            s.color = RED;
                            s.left.color = BLACK;
                            rotateRight(s);
                            // 得到2.1.2.1，继续处理
                        } else {
                            // 2.1.2.3 兄弟节点的左右节点都是黑色（此时两个节点都是null）
                            s.color = RED;
                            // p作为新的替换节点，继续处理
                            node = p;
                        }
                    }
                }
            } else {
                // 2.2 是父节点的右节点
                s = p.left;
                if (s.color == RED) {
                    // 2.2.1 兄弟节点是红节点
                    s.color = BLACK;
                    p.right.color = RED;
                    rotateRight(p);
                    break;
                } else {
                    // 2.2.2 兄弟节点是黑节点
                    if (colorOf(s.left) == RED) {
                        // 2.2.2.1 兄弟节点的左节点是红节点(必定有两个红子节点)
                        s.color = p.color;
                        p.color = BLACK;
                        s.left.color = BLACK;
                        rotateRight(p);
                        break;
                    } else {
                        if (colorOf(s.left) == BLACK && colorOf(s.right) == RED) {
                            // 2.2.2.2 兄弟节点的左节点是黑节点，右节点是红节点
                            s.color = RED;
                            s.right.color = BLACK;
                            rotateLeft(s);
                            // 得到2.2.2.1，继续处理
                        } else {
                            // 2.2.2.3 兄弟节点的左右节点都是黑节点（此时两个子节点都是null）
                            s.color = RED;
                            // 把p作为新的替换节点，继续处理
                            node = p;
                        }
                    }
                }
            }
        }
        // 情景1 如果是红色节点，置为黑色
        node.color = BLACK;
    }

    /**
     * 寻找最终替换节点
     *
     * @return 替换节点，必定是树末节点
     */
    private RBTreeNode findReplaceNode(RBTreeNode node) {
        RBTreeNode replacement;
        if (node.left == null && node.right == null) {
            // 情景1 两个节点为空，替换节点为本身
            replacement = node;
        } else if (node.left != null && node.right != null) {
            // 情景3 两个节点非空，替换节点是后继节点
            replacement = successor(node);
            node.key = replacement.key;

            if (replacement.right != null) {
                // 如果后继节点有右节点，转为情形2
                RBTreeNode rr = replacement.right;
                replacement.key = rr.key;
                replacement = rr;
            }
        } else {
            // 情景2 有一个子节点非空，用子节点替换
            if (node.left != null) {
                replacement = node.left;
            } else {
                replacement = node.right;
            }
            node.key = replacement.key;
        }
        return replacement;
    }

    private RBTreeNode successor(RBTreeNode node) {
        if (node == null) {
            return null;
        } else if (node.right != null) {
            // 右子树非空，后继节点是右子树最小节点
            RBTreeNode n = node.right;
            while (n.left != null) {
                n = n.left;
            }
            return n;
        } else {
            // 右子树为空，后继节点是第一个往左走的祖先
            RBTreeNode p = node.parent;
            RBTreeNode c = node;
            while (p != null && c == p.right) {
                c = p;
                p = p.parent;
            }
            return p;
        }
    }

    /**
     * 对P点左旋
     *      pp                 pp
     *      /                  /
     *     p         -->      r
     *    / \                / \
     *   l   r              p   rr
     *      / \            / \
     *     rl  rr         l  rl
     * 旋转过程中p-l，r-rr不变，pp以上结构不变
     */
    private void rotateLeft(RBTreeNode p) {
        if (p != null) {
            RBTreeNode r, rl, pp;
            r = p.right;
            rl = r.left;
            pp = p.parent;

            if (pp == null) {
                // 如果是根节点左旋，把r设为根节点
                root = r;
            } else {
                if (p == pp.left) {
                    pp.left = r;
                } else {
                    pp.right = r;
                }
            }
            r.parent = pp;

            r.left = p;
            p.parent = r;

            p.right = rl;
            if (rl != null) {
                rl.parent = p;
            }
        }
    }

    /**
     * 对P点右旋
     *      pp             pp
     *      |              |
     *      p      -->     l
     *    /  \           /  \
     *   l    r        ll    p
     *  / \                 / \
     * ll lr              lr   r
     * 右旋过程中l-ll、p-r不变，pp以上结构保持不变
     */
    private void rotateRight(RBTreeNode p) {
        if (p != null) {
            RBTreeNode l, lr, pp;
            l = p.left;
            lr = l.right;
            pp = p.parent;

            if (pp == null) {
                // 如果是根节点右旋，bal设为根节点
                root = l;
            } else {
                if (p == pp.left) {
                    pp.left = l;
                } else {
                    pp.right = l;
                }
            }
            l.parent = pp;

            l.right = p;
            p.parent = l;

            p.left = lr;
            if (lr != null) {
                lr.parent = p;
            }
        }
    }

    public static boolean colorOf(RBTreeNode p) {
        return p == null ? BLACK : p.color;
    }

    public static final boolean RED = false;
    public static final boolean BLACK = true;

    static class RBTreeNode {
        int key;
        RBTreeNode left;
        RBTreeNode right;
        RBTreeNode parent;
        /* 默认为红色，不破坏黑色平衡，这样就不需要自平衡 */
        boolean color = RED;

        RBTreeNode(int key) {
            this.key = key;
        }

        RBTreeNode(int key, boolean color) {
            this.key = key;
            this.color = color;
        }
    }
}
