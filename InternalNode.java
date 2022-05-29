
import java.util.ArrayList;
import java.util.List;

/**
 * Internal/Index nodes which contain the keys used for indexing and the
 * pointers/children that references
 * Internal nodes or Leaf nodes
 */
public class InternalNode<K extends Comparable<K>> extends Node<K> {

    private List<Node<K>> children;

    InternalNode(int order) {
        // eg. order = 3
        this.order = order;
        // eg. 2 keys per node
        this.keys = new ArrayList<>();
        // eg. 3 children/pointers
        this.children = new ArrayList<>();
        this.nodeType = NodeType.INTERNAL;
    }

    public List<Node<K>> getChildren() {
        return this.children;
    }

    public int getIndex(Node<K> node) {
        return children.indexOf(node);
    }

    /**
     * Gets the index to insert the new key and then adds it. Checks if an overflow
     * occurred and calls the function
     * to handle it if it did
     */
    public void insertKey(K key) {
        int insertIndex = getInsertionIndex(key);
        // Ensures the keys stay in sorted order
        keys.add(insertIndex, key);

        if (isOverflow()) {
            handleOverflow();
        }
    }

    /**
     * Many things occur when an overflow occurs:
     * 1. A new node is created, in this case since the overflow is occurring in an
     * Internal node a new Internal node
     * will be created
     *
     * 2. The children of the current Internal node (left) will be split between
     * itself and the new node
     *
     * 3. The keys of the current Internal node will be split between itself and the
     * new node
     *
     * 4. Set the parent for the new Internal node to be the same as the current
     * Internal node
     *
     * 5. Update the children/pointers for the parent node. The current (left)
     * Internal node will maintain its current
     * position in the parents pointers/children list while the new node will be
     * added next to it. Every other node
     * after this will be pushed back
     *
     * NOTE: Since we are using ArrayList instead of Vector the other entries in the
     * array list are not pushed back,
     * since ArrayList uses doubly linked list internally
     *
     * 6. Insert/Push the middle key (the first key in the right/new node) up to the
     * parent
     */
    @Override
    public void handleOverflow() {
        // Because decimals get truncated automatically no need to do ceiling and -1 to
        // get the middle index
        int mid = this.keys.size() / 2;
        K midKey = this.keys.get(mid);

        // 1. Create new internal node
        InternalNode<K> newNodeRight = new InternalNode<>(order);

        // 2. Split the children between the current node and the newRightNode
        List<Node<K>> leftChildNodes = new ArrayList<>(children.subList(0, (int) Math.ceil(children.size() / 2.0)));
        List<Node<K>> rightChildNodes = new ArrayList<>(
                children.subList((int) Math.ceil(children.size() / 2.0), children.size()));
        this.children = leftChildNodes;
        newNodeRight.children = rightChildNodes;

        // 3. Split the keys between the current node and the newRightNode
        List<K> keysLeft = new ArrayList<>(keys.subList(0, mid));
        // The + 1 here is because in Internal nodes the first key in the right/new node
        // is pushed up to the parent
        List<K> keysRight = new ArrayList<>(keys.subList(mid + 1, keys.size()));
        this.keys = keysLeft;
        newNodeRight.keys = keysRight;

        // 4. Create a parent node (parents must always be internal nodes) if it does
        // not exist
        if (this.getParent() == null) {
            this.setParent(new InternalNode<>(order));
            this.parent.children.add(0, this);
        }
        newNodeRight.setParent(this.getParent());

        // 5. Set the children for the parent
        int newChildIndex = this.parent.getIndex(this) + 1;
        // the left (current) node maintains its position
        // the right (new) node takes up the next space in array (hence the +1 above)
        // every other child gets pushed to the right
        this.parent.children.add(newChildIndex, newNodeRight);
        // update the parents for the children of the new node
        for (Node<K> child : newNodeRight.getChildren()) {
            child.setParent(newNodeRight);
        }

        // 6. push up a key to parent internal node
        this.parent.insertKey(midKey);
    }
}
