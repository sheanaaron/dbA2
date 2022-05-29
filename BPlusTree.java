
import java.util.ArrayList;
import java.util.List;

public class BPlusTree<K extends Comparable<K>, V> {

    // Must be >= 3
    // is the maximum number of children a node can have
    private int order;

    private Node<K> root;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new LeafNode<K, V>(order);
    }

    /**
     * Basic node insertion, traverses down the tree until it finds a leaf node.
     * Inserts the leaf node into the tree and then updates the root if it has
     * changed.
     */
    public void insert(K key, V data) {
        // Traverse the tree until you reach a leaf node
        LeafNode<K, V> leafNode = findLeafNode(key);
        leafNode.insert(key, data);

        // Check if the root node has changed
        resetRoot();
    }

    /**
     * We assume that the heap file is already sorted prior to bulk loading
     *
     * This algorithm will load all entries into the binary tree in a more
     * efficient manner, but will do so all in one go.
     *
     * The root of the tree will be the rightmost leaf node and will insert
     * each new key/record into this node. When overflow occurs and a new leaf
     * node is added to the right of the current one, the new rightmost node
     * will be the new root. The root will be set to the highest parent after
     * all records are entered
     */
    public void bulkInsert(K key, V data) {
        LeafNode<K, V> bulkLoadRoot = (LeafNode<K, V>) root;
        bulkLoadRoot.insert(key, data);
        if (bulkLoadRoot.getRightSibling() != null) {
            this.root = bulkLoadRoot.getRightSibling();
        }
    }

    /**
     * Finds the leaf node by traversing to the bottom of the tree using the key
     */
    private LeafNode<K, V> findLeafNode(K key) {
        Node<K> node = this.root;
        while (node.getNodeType() != NodeType.LEAF) {
            // Uses a binary search to find the right child node to traverse to
            int index = node.getInsertionIndex(key);

            InternalNode<K> internalNode = (InternalNode<K>) node;
            node = internalNode.getChildren().get(index);
        }
        return (LeafNode<K, V>) node;
    }

    public V search(K key) {
        LeafNode<K, V> leaf = findLeafNode(key);
        return leaf.search(key);
    }

    public List<V> rangeSearch(K searchKeyBot, K searchKeyTop) {
        List<V> rangeResult = new ArrayList<>();
        // Start from the bottom bound and go up
        LeafNode<K, V> leaf = findLeafNode(searchKeyBot);
        boolean upperBoundReached = false;

        while (!upperBoundReached) {
            List<K> currentLeafKeys = leaf.getKeys();
            //
            for (int i = 0; i < currentLeafKeys.size(); i++) {
                // if the current key is greater than the bottom bound
                if (currentLeafKeys.get(i).compareTo(searchKeyBot) >= 0) {
                    // if the current key is less than the upper bound
                    if (currentLeafKeys.get(i).compareTo(searchKeyTop) > 0) {
                        upperBoundReached = true;
                        break;
                    }
                    rangeResult.add(leaf.getDataEntries().get(i));
                }
            }
            leaf = leaf.getRightSibling();
        }
        return rangeResult;
    }

    public void resetRoot() {
        while (root.getParent() != null) {
            this.root = root.getParent();
        }
    }
}
