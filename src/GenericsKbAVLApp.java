import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.*;

public class GenericsKbAVLApp {
    private Node root;
    private Scanner scanner;
    // private int size;

    public GenericsKbAVLApp(Scanner scanner) {
        this.root = null;
        this.scanner = scanner;
    }

    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            int choice = this.getChoice();

            switch(choice) {
                case 1:
                    try {
                        loadKnowledgeBase();
                    } catch (IOException e) {
                        ConsoleStyle.printError("Failed to load knowledge base: " + e.getMessage());
                    }
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

    public int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private Node rightRotate(Node node) {

        Node node1 = node.left;
        Node node2 = node1.right;

        node1.right = node;
        node.left = node2;

        updateHeight(node);
        updateHeight(node1);

        return node1;
    }

    private Node leftRotate(Node node) {
        Node node1 = node.right;
        Node node2 = node1.left;

        node1.left = node;
        node.right = node2;

        updateHeight(node);
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


    private void insert(Statement statement) {
        root = insertHelper(statement, this.root);
    }

    private Node balance(Node root) {

        updateHeight(root);

        if (balanceFactor(root) == 2) {
            if (balanceFactor(root.left) >= 0) { 
                root.left = leftRotate(root.left);
                return leftRotate(root);
            }

        }

        if (balanceFactor(root) == -2) {
            if (balanceFactor(root.right) <= 0) {
                root.left = leftRotate(root.left);
                return rightRotate(root);
            }
        }

        return root;
    }

    private Node insertHelper(Statement statement, Node root) {
        if (root == null) return new Node(statement);

        if (statement.compareTo(root.statement) <= 0) {
            root.left = insertHelper(statement, root.left);
        } else if (statement.compareTo(root.statement) > 0) {
            root.right = insertHelper(statement, root.right);
        }

        return balance(root);
    }

    private int balanceFactor(Node node) {
        return node == null ? 0 : height(node.right) - height(node.left);
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

}
