
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Leaf nodes of the B+ Tree, cannot be parents hence no children, maintains
 * a list of data entries whose index
 * corresponds the the key
 */
public class LeafNode<K extends Comparable<K>, V> extends Node<K> {

    private LeafNode<K, V> rightSibling;
    private LeafNode<K, V> leftSibling;

    // Sync this with the keys
    // private List<L> keys from Node class
    private List<V> dataEntries;

    LeafNode(int order) {
        this.keys = new ArrayList<>();
        this.dataEntries = new ArrayList<>();
        this.order = order;
        this.nodeType = NodeType.LEAF;
    }

    public void insert(K key, V data) {
        int insertIndex = getInsertionIndex(key);
        // Ensures the keys stay in sorted order
        keys.add(insertIndex, key);
        // Ensure the data entry uses has the same index as its key
        dataEntries.add(insertIndex, data);
        if (isOverflow()) {
            handleOverflow();
        }
    }

    /**
     * Handles overflow once there are too many entries in a Leaf node
     * 1. A new node is created, in this case since the overflow is occurring in an
     * Leaf node a new Leaf node will
     * be created and set to the right of this one. The keys and their corresponding
     * data entries will be split
     * between the current leaf node and the new one
     *
     * 2. Create a Internal node parent as only Internal nodes can be parents and
     * set the parent of the new Leaf node
     * to match the current Leaf node
     *
     * 3. Update the parents children/pointers
     *
     * 4. Update the sibling relationship pointers, the current Leaf nodes right
     * sibling (if it exists) will not be the
     * right sibling of the newly created Leaf node and consequently the right
     * sibling of the currentLeaf node will
     * have its left sibling updated to point to the newly created Leaf node. etc
     *
     * 5. The middle key (the first key in the new/right Leaf node) will be pushed
     * up to the parent
     */
    @Override
    public void handleOverflow() {
        // Because decimals get truncated automatically no need to do ceiling and -1 to
        // get the middle index
        int mid = this.keys.size() / 2;
        K midKey = this.keys.get(mid);

        // 1. Create a new node for the right sibling
        LeafNode<K, V> newNodeRight = new LeafNode<>(order);

        // Split the keys and data entries into two arrays for the two nodes
        List<K> keysLeft = new ArrayList<>(keys.subList(0, mid));
        List<K> keysRight = new ArrayList<>(keys.subList(mid, keys.size()));
        List<V> dataEntriesLeft = new ArrayList<>(dataEntries.subList(0, mid));
        List<V> dataEntriesRight = new ArrayList<>(dataEntries.subList(mid, dataEntries.size()));

        // Set the keys and their data entries
        this.keys = keysLeft;
        this.dataEntries = dataEntriesLeft;
        newNodeRight.keys = keysRight;
        newNodeRight.dataEntries = dataEntriesRight;

        // 2. Create a parent node (parents must always be internal nodes)
        // if it does not already exist
        if (this.getParent() == null) {
            this.setParent(new InternalNode<>(order));
            this.parent.getChildren().add(0, this);
        }
        newNodeRight.setParent(this.getParent());

        // 3. Add the nodes to the parents children
        int newChildIndex = this.parent.getIndex(this) + 1;
        // the left (current) node maintains its position
        // the right (new) node takes up the next space in array
        // every other child gets pushed to the right
        this.parent.getChildren().add(newChildIndex, newNodeRight);

        // 4. prev and next nodes (sibling nodes) need to be set and maintained
        if (this.rightSibling != null) {
            this.rightSibling.setLeftSibling(newNodeRight);
            newNodeRight.setRightSibling(this.rightSibling);
        }
        newNodeRight.setLeftSibling(this);
        this.setRightSibling(newNodeRight);

        // push up a key to parent internal node
        this.parent.insertKey(midKey);
    }

    public V search(K key) {
        V result = null;
        int index = Collections.binarySearch(keys, key);
        if (index >= 0) {
            result = dataEntries.get(index);
        }
        return result;
    }

    public void setRightSibling(LeafNode<K, V> rightSibling) {
        this.rightSibling = rightSibling;
    }

    public void setLeftSibling(LeafNode<K, V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    public LeafNode<K, V> getRightSibling() {
        return this.rightSibling;
    }

    public List<V> getDataEntries() {
        return this.dataEntries;
    }

    public List<K> getKeys() {
        return this.keys;
    }
}
