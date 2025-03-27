import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.*;

public class GenericsAVLApp {
    private Node root;
    private Scanner scanner;

    // This is the terms for querying.
    private String[] terms;
    // private int size;

    public GenericsAVLApp(Scanner scanner) {
        this.terms = new String[15000];
        this.root = null;
        this.scanner = scanner;
    }

    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            displayMenu();
            int choice = this.getChoice();


            switch(choice) {
                case 1:
                    try {
                        loadKnowledgeBase();
                    } catch (IOException e) {
                        ConsoleStyle.printError("Failed to load knowledge base: " + e.getMessage());
                    }
                    break;

                case 2:
                    searchEachTerm();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        ConsoleStyle.clearScreen();
        ConsoleStyle.printHeader();
        
        System.out.println(ConsoleStyle.BOLD + "Choose an action from the menu:\n" + ConsoleStyle.RESET);
        System.out.println(ConsoleStyle.CYAN + "1. " + ConsoleStyle.RESET + "Load a knowledge base from a file");
        System.out.println(ConsoleStyle.CYAN + "2. " + ConsoleStyle.RESET + "Search terms from query file");
        // System.out.println(ConsoleStyle.CYAN + "3. " + ConsoleStyle.RESET + "Search for a statement in the knowledge base by term");
        // System.out.println(ConsoleStyle.CYAN + "4. " + ConsoleStyle.RESET + "Search for a statement in the knowledge base by term and sentence");
        System.out.println(ConsoleStyle.CYAN + "5. " + ConsoleStyle.RESET + "Save knowledge base to file");
        System.out.println(ConsoleStyle.CYAN + "6. " + ConsoleStyle.RESET + "Quit");
        ConsoleStyle.printDivider();
    }

    public int height(Node node) {
        return node == null ? 0 : node.height;
    }

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

    private int getChoice() {
        return this.scanner.nextInt();
    }


    private void loadKnowledgeBase() throws IOException {
        scanner.nextLine();
        String fileName = this.scanner.nextLine();

        File file = new File(fileName);

        if (!file.exists()) {
            file = new File("src/" + fileName);
        }

        try {
            if (!file.exists()) {
                ConsoleStyle.printError("File not found: " + fileName);
                System.out.println("Current directory: " + new File(".").getAbsolutePath());
                return;
            }
            ConsoleStyle.printSuccess("Found file: " + file.getAbsolutePath());
            loadFile(file.getPath());
            ConsoleStyle.printSuccess("Knowledge base loaded successfully.");
        } catch(IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
    }

    private void loadFile(String file) throws IOException {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) insert(statement);
            }

        } catch (IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
        
    }


    private void loadTerms(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int index = 0;
            while ((line = reader.readLine())!= null) {
                terms[index++] = line.trim();
            }

        } catch (IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
    }

    private void insert(Statement statement) {
        root = insertHelper(statement, this.root);
    }

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

    private Node insertHelper(Statement statement, Node root) {
        if (root == null) return new Node(statement);

        if (statement.getTerm().compareTo(root.statement.getTerm()) <= 0) {
            root.left = insertHelper(statement, root.left);
        } else if (statement.getTerm().compareTo(root.statement.getTerm()) > 0) {
            root.right = insertHelper(statement, root.right);
        }

        return balance(root);
    }

    private int balanceFactor(Node node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        if (node == null) return;
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    // O(n)
    private void searchEachTerm() {
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter queries file name: ");
        String fileName = scanner.nextLine();
        
        loadTerms(fileName);
        
        for (String term : terms) {
            if (term == null) break; // Stop when we reach end of terms
            Node result = searchHelper(this.root, term.trim());
            if (result != null) {
                Statement _statement = result.statement;
                System.out.printf("%s: %s (%.1f)%n", 
                    _statement.getTerm(), 
                    _statement.getSentence(), 
                    _statement.getConfidenceScore());
            } else {
                System.out.println("Term not found: " + term);
            }
        }
    }
 
    // O(log(n))
    private Node searchHelper(Node root, String term) {
        if (root == null) {
            return null;
        }

        if (root.statement.getTerm().equals(term)) return root;

        if (term.compareTo(root.statement.getTerm()) > 0) {
            return searchHelper(root.right, term);
        }

        return searchHelper(root.left, term);
    }

}
