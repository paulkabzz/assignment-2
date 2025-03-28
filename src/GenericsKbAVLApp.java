import java.io.*;
import java.util.*;

public class GenericsKbAVLApp {
    // Instrumentation counters with static methods to track
    private static int insertComparisonCount = 0;
    private static int searchComparisonCount = 0;

    private Node root;

    // Node class for AVL Tree
    private class Node {
        Statement statement;
        Node left, right;
        int height;

        Node(Statement statement) {
            this.statement = statement;
            this.height = 1;
        }
    }

    // Static method to reset counters
    public static void resetComparisonCounters() {
        insertComparisonCount = 0;
        searchComparisonCount = 0;
    }

    // Static methods to get comparison counts
    public static int getInsertComparisonCount() {
        return insertComparisonCount;
    }

    public static int getSearchComparisonCount() {
        return searchComparisonCount;
    }

    // Modify insert to use static counter
    public void insert(Statement statement) {
        root = insertRecursive(root, statement);
    }

    private Node insertRecursive(Node current, Statement statement) {
        // Base case: create new node
        if (current == null) {
            return new Node(statement);
        }

        // Increment insert comparison count
        insertComparisonCount++;

        // Compare and decide which subtree to traverse
        if (statement.getTerm().compareTo(current.statement.getTerm()) <= 0) {
            current.left = insertRecursive(current.left, statement);
        } else {
            current.right = insertRecursive(current.right, statement);
        }

        // Update height
        current.height = 1 + Math.max(getHeight(current.left), getHeight(current.right));

        // Balance the tree
        return balance(current);
    }

    // Search method with static counter
    public Node search(String term) {
        return searchRecursive(root, term);
    }

    private Node searchRecursive(Node current, String term) {
        // Base cases
        if (current == null) {
            return null;
        }

        // Increment search comparison count
        searchComparisonCount++;

        // Compare current node's term
        int comparisonResult = term.compareTo(current.statement.getTerm());

        if (comparisonResult == 0) {
            return current;
        } else if (comparisonResult < 0) {
            return searchRecursive(current.left, term);
        } else {
            return searchRecursive(current.right, term);
        }
    }

    // Balancing methods
    private Node balance(Node node) {
        if (node == null) return null;

        // Get balance factor
        int balance = getBalanceFactor(node);

        // Left heavy
        if (balance > 1) {
            if (getBalanceFactor(node.left) < 0) {
                node.left = leftRotate(node.left);
            }
            return rightRotate(node);
        }

        // Right heavy
        if (balance < -1) {
            if (getBalanceFactor(node.right) > 0) {
                node.right = rightRotate(node.right);
            }
            return leftRotate(node);
        }

        return node;
    }

    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));

        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y;
    }

    // Utility methods
    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalanceFactor(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }

    // Main method to run the application
    public static void main(String[] args) {
        GenericsKbAVLApp app = new GenericsKbAVLApp();
        app.runApplication();
    }

    public void runApplication() {
        try {
            // Reset counters before loading
            resetComparisonCounters();

            // Load knowledge base
            loadKnowledgeBase("src/GenericsKB.txt");

            // Process queries
            processQueries("src/GenericsKB-queries.txt");

            // Print comparison counts
            System.out.println("Insert Comparison Count: " + getInsertComparisonCount());
            System.out.println("Search Comparison Count: " + getSearchComparisonCount());
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    private void loadKnowledgeBase(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) {
                    insert(statement);
                }
            }
        }
    }

    private void processQueries(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String term;
            while ((term = reader.readLine()) != null) {
                term = term.trim();
                Node result = search(term);
                
                if (result != null) {
                    Statement statement = result.statement;
                    System.out.printf("%s: %s (%.1f)%n", 
                        statement.getTerm(), 
                        statement.getSentence(), 
                        statement.getConfidenceScore());
                } else {
                    System.out.println("Term not found: " + term);
                }
            }
        }
    }
}