import java.io.*;
import java.util.*;

/**
 * Specialized AVL tree implementation for experiment instrumentation
 */
public class InstrumentedAVLTree {
    private Node root;
    private long currentInsertComparisons;
    private long currentSearchComparisons;
    
    /**
     * Loads statements from a file and returns a list of comparison counts
     */
    public List<Long> loadFromFile(String fileName) {
        List<Long> insertComparisons = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) {
                    currentInsertComparisons = 0;
                    insert(statement);
                    insertComparisons.add(currentInsertComparisons);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
        
        return insertComparisons;
    }
    
    /**
     * Searches for multiple terms and returns a list of comparison counts
     */
    public List<Long> searchTerms(String[] terms) {
        List<Long> searchComparisons = new ArrayList<>();
        
        for (String term : terms) {
            if (term == null) continue;
            
            currentSearchComparisons = 0;
            search(term.trim());
            searchComparisons.add(currentSearchComparisons);
        }
        
        return searchComparisons;
    }
    
    /**
     * Inserts a statement into the tree
     */
    private void insert(Statement statement) {
        root = insertHelper(statement, root);
    }
    
    /**
     * Helper method for insertion with instrumentation
     */
    private Node insertHelper(Statement statement, Node root) {
        if (root == null) return new Node(statement);

        currentInsertComparisons++; // Count comparison
        if (statement.getTerm().compareTo(root.statement.getTerm()) <= 0) {
            root.left = insertHelper(statement, root.left);
        } else {
            currentInsertComparisons++; // Count comparison
            root.right = insertHelper(statement, root.right);
        }

        return balance(root);
    }
    
    /**
     * Searches for a term in the tree
     */
    private Node search(String term) {
        return searchHelper(root, term);
    }
    
    /**
     * Helper method for search with instrumentation
     */
    private Node searchHelper(Node root, String term) {
        if (root == null) {
            return null;
        }

        currentSearchComparisons++; // Count comparison
        if (root.statement.getTerm().equals(term)) return root;
    
        currentSearchComparisons++; // Count comparison
        if (term.compareTo(root.statement.getTerm()) > 0) {
            return searchHelper(root.right, term);
        }
    
        return searchHelper(root.left, term);
    }
    
    /**
     * Balances the tree after insertion
     */
    private Node balance(Node root) {
        if (root == null) return null;
        
        updateHeight(root);
        int balance = balanceFactor(root);

        // Left heavy
        if (balance > 1) {
            // Left-Right case
            if (balanceFactor(root.left) < 0) {
                root.left = leftRotate(root.left);
            }
            // Left-Left case
            return rightRotate(root);
        }

        // Right heavy
        if (balance < -1) {
            // Right-Left case
            if (balanceFactor(root.right) > 0) {
                root.right = rightRotate(root.right);
            }
            // Right-Right case
            return leftRotate(root);
        }

        return root;
    }
    
    /**
     * Gets the height of a node
     */
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }
    
    /**
     * Updates the height of a node
     */
    private void updateHeight(Node node) {
        if (node == null) return;
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }
    
    /**
     * Calculates the balance factor of a node
     */
    private int balanceFactor(Node node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }
    
    /**
     * Performs a right rotation
     */
    private Node rightRotate(Node root) {
        if (root == null || root.left == null) return root;
        
        Node node1 = root.left;
        Node node2 = node1.right;

        // Perform rotation
        node1.right = root;
        root.left = node2;

        // Update heights
        updateHeight(root);
        updateHeight(node1);

        return node1;
    }
    
    /**
     * Performs a left rotation
     */
    private Node leftRotate(Node root) {
        if (root == null || root.right == null) return root;
        
        Node node1 = root.right;
        Node node2 = node1.left;

        // Perform rotation
        node1.left = root;
        root.right = node2;

        // Update heights
        updateHeight(root);
        updateHeight(node1);

        return node1;
    }
    
    /**
     * Node class for the AVL tree
     */
    private static class Node {
        Statement statement;
        Node left, right;
        int height = 1;
        
        Node(Statement statement) {
            this.statement = statement;
        }
    }
}