/**
 * Represents a node in the Binary Search Tree
 */
public class Node {
    /** The statement stored in this node */
    Statement statement;
    /** Left and right children nodes */
    Node left, right;

    int height;

    /**
     * Creates a new node with given statement
     * @param statement Statement to store in node
     */
    public Node(Statement statement) {
        this.statement = statement;
        this.left = this.right = null;
        this.height = 1;
    }
}
