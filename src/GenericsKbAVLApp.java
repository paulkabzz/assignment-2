import java.io.*;

/**
 * AVL Tree implementation of the GenericsKB knowledge base.
 * This class implements a self-balancing AVL tree data structure
 * to store and retrieve statements efficiently.
 * It includes instrumentation for counting key comparisons during
 * insert and search operations for performance analysis.
 * 
 * This datastructure was implemented by GeeksForGeeks and modified to fit this project. Some of the implementation was also taken from the notes.
 * https://www.geeksforgeeks.org/avl-tree-program-in-java/
 */

public class GenericsKbAVLApp {
    private static int insertComparisonCount = 0;
    private static int searchComparisonCount = 0;

    private Node root;

 /**
     * Resets the comparison counters to zero.
     * Should be called before starting a new experiment.
     */
    public static void resetComparisonCounters() {
        insertComparisonCount = 0;
        searchComparisonCount = 0;
    }

    /**
     * Returns the current insert comparison count.
     * 
     * @return The number of key comparisons performed during insert operations
     */
    public static int getInsertComparisonCount() {
        return insertComparisonCount;
    }

    /**
     * Returns the current search comparison count.
     * 
     * @return The number of key comparisons performed during search operations
     */
    public static int getSearchComparisonCount() {
        return searchComparisonCount;
    }

    /**
     * Inserts a statement into the AVL tree.
     * 
     * @param statement The statement to insert
     */
    public void insert(Statement statement) {
        root = insertRecursive(root, statement);
    }

    /**
     * Recursively inserts a statement into the AVL tree.
     * 
     * @param current The current node being examined
     * @param statement The statement to insert
     * @return The updated node after insertion
     */
    private Node insertRecursive(Node current, Statement statement) {
        if (current == null) {
            return new Node(statement);
        }

        insertComparisonCount++;

        int comparisonResult = statement.getTerm().compareTo(current.statement.getTerm());
        
        if (comparisonResult <= 0) {
            current.left = insertRecursive(current.left, statement);
        } else {
            current.right = insertRecursive(current.right, statement);
        }
    
        current.height = 1 + Math.max(getHeight(current.left), getHeight(current.right));
    
        return balance(current);
    }

    /**
     * Searches for a statement with the given term.
     * 
     * @param term The term to search for
     * @return The node containing the statement if found, null otherwise
     */
    public Node search(String term) {
        return searchRecursive(root, term);
    }

    /**
     * Recursively searches for a statement with the given term.
     * 
     * @param current The current node being examined
     * @param term The term to search for
     * @return The node containing the statement if found, null otherwise
     */
    private Node searchRecursive(Node current, String term) {
        if (current == null) {
            return null;
        }

        int comparisonResult = term.compareTo(current.statement.getTerm());
        
        searchComparisonCount++;
    
        if (comparisonResult == 0) {
            return current;
        } else if (comparisonResult < 0) {
            return searchRecursive(current.left, term);
        } else {
            return searchRecursive(current.right, term);
        }
    }

    /**
     * Balances the tree at the given node if necessary.
     * 
     * @param node The node to check for balance
     * @return The balanced node
     */
    private Node balance(Node node) {
        if (node == null) return null;
    
        int balance = getBalanceFactor(node);
    
        if (balance > 1) {
            if (getBalanceFactor(node.left) < 0) {
                node.left = leftRotate(node.left);
            }
            return rightRotate(node);
        }
    
        if (balance < -1) {
            if (getBalanceFactor(node.right) > 0) {
                node.right = rightRotate(node.right);
            }
            return leftRotate(node);
        }
    
        return node;
    }

    /**
     * Performs a right rotation on the given node.
     * 
     * @param y The node to rotate
     * @return The new root after rotation
     */
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));

        return x;
    }

    /**
     * Performs a left rotation on the given node.
     * 
     * @param x The node to rotate
     * @return The new root after rotation
     */
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));

        return y;
    }

    /**
     * Gets the height of a node.
     * 
     * @param node The node to get the height of
     * @return The height of the node, or 0 if the node is null
     */
    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Calculates the balance factor of a node.
     * 
     * @param node The node to calculate the balance factor for
     * @return The balance factor (left height - right height)
     */
    private int getBalanceFactor(Node node) {
        return node == null ? 0 : getHeight(node.left) - getHeight(node.right);
    }
    
    /**
     * Runs the application with the specified input and query files.
     * 
     * @param inputFile Path to the knowledge base file
     * @param queryFile Path to the query terms file
     */
    public void runApplication(String inputFile, String queryFile) {
        try {
            resetComparisonCounters();
    
            System.out.println("Loading knowledge base from: " + inputFile);
            loadKnowledgeBase(inputFile);
    
            System.out.println("Processing queries from: " + queryFile);
            processQueries(queryFile);
    
            System.out.println("Insert Comparison Count: " + getInsertComparisonCount());
            System.out.println("Search Comparison Count: " + getSearchComparisonCount());
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
    
    /**
     * Runs the application with default input and query files.
     */
    public void runApplication() {
        // Prompt user for input and query files
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Enter knowledge base file path (or press Enter for default 'src/GenericsKB.txt'): ");
            String inputFile = consoleReader.readLine().trim();
            if (inputFile.isEmpty()) {
                inputFile = "src/GenericsKB.txt";
            }
            
            System.out.print("Enter query file path (or press Enter for default 'src/GenericsKB-queries.txt'): ");
            String queryFile = consoleReader.readLine().trim();
            if (queryFile.isEmpty()) {
                queryFile = "src/GenericsKB-queries.txt";
            }
            
            runApplication(inputFile, queryFile);
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            // Fall back to defaults if there's an error
            runApplication("src/GenericsKB.txt", "src/GenericsKB-queries.txt");
        }
    }

    /**
     * Loads the knowledge base from a file.
     * 
     * @param filename Path to the knowledge base file
     * @throws IOException If an I/O error occurs
     */
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

    /**
     * Processes queries from a file.
     * 
     * @param filename Path to the query terms file
     * @throws IOException If an I/O error occurs
     */
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