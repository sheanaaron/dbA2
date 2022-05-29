
import java.util.Collections;
import java.util.List;

public abstract class Node<K extends Comparable<K>> {
    // Order/Branching factor M
    // Tree depth is logM(N)
    // Each node can have M - 1 keys
    // Each internal node can have M pointers/children

    protected List<K> keys;
    protected InternalNode<K> parent;
    protected int order;
    protected NodeType nodeType;

    /**
     * Utilizes the binarySearch method of the Collections framework to quickly find
     * the insertion point for new keys
     * The binary search upon failing (which it will do) will return the position
     * where the key would have been inserted
     *
     * We do need to process it slightly though (+ 1 to the result then get the
     * absolute of it) to find the exact index
     */
    public int getInsertionIndex(K key) {
        // Use binary search to find the insertion index instead of traversing through
        // all the keys one by one
        // Cost is log(n) instead of n
        int index = Collections.binarySearch(keys, key);
        return Math.abs(index + 1);
    }

    // When overflow occurs, split the node, etc ...
    public boolean isOverflow() {
        return this.keys.size() >= order;
    }

    public NodeType getNodeType() {
        return this.nodeType;
    }

    public InternalNode<K> getParent() {
        return this.parent;
    }

    public void setParent(InternalNode<K> parent) {
        this.parent = parent;
    }

    public abstract void handleOverflow();
}