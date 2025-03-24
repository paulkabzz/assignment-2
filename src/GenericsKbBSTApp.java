import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

/**
 * BST implementation of the GenericsKB knowledge base
 * 
 * This implementation was developed with guidance from Joel Chigumba, his github is https://github.com/joesome-git.
 * Special thanks for their insights and assistance in refining the design.
 * 
 * @author Paul Kabulu
 * Credit to Joel Chigumba - Provided valuable input and assistance.
 */
public class GenericsKbBSTApp {
    private Node root;
    private Scanner scanner;
    private int size = 0;
    private Timer timer;

    /**
     * Constructs a new BST-based knowledge base
     * @param scanner Scanner for user input
     */
    public GenericsKbBSTApp(Scanner scanner) {
        this.root = null;
        this.scanner = scanner;
        this.timer = new Timer();
        
        // Clear screen and show welcome message
        ConsoleStyle.clearScreen();
        ConsoleStyle.printAppWelcome("BST Implementation");
        
        Thread loadingThread = ConsoleStyle.startLoading("Loading BST application");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        ConsoleStyle.stopLoading(loadingThread);
        
        // Pause to show welcome message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }

    /**
     * This function retrieves a user's choice from stdin.
     *
     * @return The integer value representing the user's choice.
     */
    private int getChoice() {
        System.out.println("Enter your choice: ");
        return this.scanner.nextInt();
    }

    /**
     * Runs the main application loop
     * Handles menu display and user operations until exit
     */
    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    try {
                        loadKnowledgeBase();
                    } catch (IOException e) {
                        System.err.println("Faild to load knowledge base. ");
                        System.err.println(e.getMessage());
                    }

                    break;
                case 2:
                    addStatement();
                    break;
                case 3:
                    searchByTerm();
                    break;
                case 4:
                    searchByTermAndSentence();
                    break;
                case 5:
                    saveKnowledgeBase();
                    break;
                case 6:
                    ConsoleStyle.printGoodbye();
                    running = false;
                    break;
                default:
                    ConsoleStyle.printError("Invalid choice. Please try again.");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        // Ignore interruption
                    }
            }
        }
    }

    /*
     * This method displays the program menu to the user.
     */
    private void displayMenu() {
        ConsoleStyle.clearScreen();
        ConsoleStyle.printHeader();
        
        System.out.println(ConsoleStyle.BOLD + "Choose an action from the menu:\n" + ConsoleStyle.RESET);
        System.out.println(ConsoleStyle.CYAN + "1. " + ConsoleStyle.RESET + "Load a knowledge base from a file");
        System.out.println(ConsoleStyle.CYAN + "2. " + ConsoleStyle.RESET + "Add a new statement to the knowledge base");
        System.out.println(ConsoleStyle.CYAN + "3. " + ConsoleStyle.RESET + "Search for a statement in the knowledge base by term");
        System.out.println(ConsoleStyle.CYAN + "4. " + ConsoleStyle.RESET + "Search for a statement in the knowledge base by term and sentence");
        System.out.println(ConsoleStyle.CYAN + "5. " + ConsoleStyle.RESET + "Save knowledge base to file");
        System.out.println(ConsoleStyle.CYAN + "6. " + ConsoleStyle.RESET + "Quit");
        ConsoleStyle.printDivider();
    }

    /**
     * Loads the knowledge base from a file specified by the user.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private void loadKnowledgeBase() throws IOException {
        scanner.nextLine(); 
        ConsoleStyle.printPrompt("Enter file name (e.g., GenericsKB.txt): ");
        String fileName = scanner.nextLine();
        
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
            loadFromFile(file.getPath());
            ConsoleStyle.printSuccess("Knowledge base loaded successfully.");
        } catch (IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Inserts or updates a statement in the BST
     * @param root Current node in traversal
     * @param statement Statement to insert
     * @return Updated node
     */
    private Node insert(Node root, Statement statement) {
        if (root == null) {
            return new Node(statement);
        }

        if (root.statement.getTerm().equals(statement.getTerm())) {
            if (statement.getConfidenceScore() > root.statement.getConfidenceScore()) {
                root.statement = statement;
            }
            return root;
        }

        if (statement.getTerm().compareTo(root.statement.getTerm()) < 0) {
            root.left = insert(root.left, statement);
        } else {
            root.right = insert(root.right, statement);
        }

        return root;
    }

    /**
     * Adds a new statement to the knowledge base
     */
    private void addStatement() {
        scanner.nextLine();
        
        ConsoleStyle.printPrompt("Enter the term: ");
        String term = scanner.nextLine();
        
        ConsoleStyle.printPrompt("Enter the statement: ");
        String sentence = scanner.nextLine();
        
        ConsoleStyle.printPrompt("Enter the confidence score: ");
        double score;
        try {
            String scoreStr = scanner.nextLine();
            score = Double.parseDouble(scoreStr);
            
            Statement newStatement = new Statement(term, sentence, score);
            insertStatement(newStatement);
            
            ConsoleStyle.printSuccess("Statement for term '" + term + "' has been updated.");
        } catch (NumberFormatException e) {
            ConsoleStyle.printError("Invalid confidence score. Please enter a valid number.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Adds a new statement to the knowledge base
     * 
     * @param statement the new statement to be added
     */
    private void insertStatement(Statement statement) {
        root = insert(root, statement);
        size++;
    }

    /**
     * Searches for a term in the BST
     * @param root Current node in traversal
     * @param term Term to search for
     * @return Node containing the term or null
     */
    private Node search(Node root, String term) {
        if (root == null || root.statement.getTerm().equals(term)) {
            return root;
        }

        if (term.compareTo(root.statement.getTerm()) > 0) {
            return search(root.right, term);
        }

        return search(root.left, term);
    }

    /**
     * Searches for a statement by term
     */
    private void searchByTerm() {
        scanner.nextLine(); // Consume newline
        
        if (isEmpty()) {
            ConsoleStyle.printError("Knowledge base is empty. Please load a file first.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        ConsoleStyle.printPrompt("Enter the term to search: ");
        String term = scanner.nextLine();
        
        timer.start();
        Thread loadingThread = ConsoleStyle.startLoading("Searching database");
        
        Node result = search(root, term);
        
        timer.stop();
        ConsoleStyle.stopLoading(loadingThread);
        
        if (result != null) {
            System.out.println("\n" + ConsoleStyle.GREEN + "Statement found:" + ConsoleStyle.RESET);
            System.out.println(ConsoleStyle.YELLOW + "Term: " + ConsoleStyle.RESET + result.statement.getTerm());
            System.out.println(ConsoleStyle.YELLOW + "Statement: " + ConsoleStyle.RESET + result.statement.getSentence());
            System.out.println(ConsoleStyle.YELLOW + "Confidence: " + ConsoleStyle.RESET + result.statement.getConfidenceScore());
        } else {
            ConsoleStyle.printError("No statement found for term: " + term);
        }
        
        ConsoleStyle.printPerformanceStats("Search operation", timer.getTimeInMilliseconds());
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Searches for a statement by both term and sentence
     */
    private void searchByTermAndSentence() {
        scanner.nextLine();
        
        if (isEmpty()) {
            ConsoleStyle.printError("Knowledge base is empty. Please load a file first.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        ConsoleStyle.printPrompt("Enter the term: ");
        String term = scanner.nextLine();
        
        ConsoleStyle.printPrompt("Enter the statement to search for: ");
        String sentence = scanner.nextLine();
        
        timer.start();
        Thread loadingThread = ConsoleStyle.startLoading("Searching database");
        
        Node result = search(root, term);
        
        timer.stop();
        ConsoleStyle.stopLoading(loadingThread);
        
        if (result != null && result.statement.getSentence().equals(sentence)) {
            System.out.println("\nThe statement was found and has a confidence score of " + result.statement.getConfidenceScore() + ".");
        } else {
            ConsoleStyle.printError("Statement not found.");
        }
        
        ConsoleStyle.printPerformanceStats("Search operation", timer.getTimeInMilliseconds());
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Saves the current knowledge base to a file
     */
    private void saveKnowledgeBase() {
        scanner.nextLine();
        System.out.print("Enter file name to save to: ");
        String fileName = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            saveInOrder(root, writer);
            System.out.println("Knowledge base saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Performs in-order traversal to save nodes to file
     * @param node Current node in traversal
     * @param writer File writer
     * @throws IOException If writing fails
     */
    private void saveInOrder(Node node, BufferedWriter writer) throws IOException {
        if (node != null) {
            saveInOrder(node.left, writer);
            writer.write(node.statement.toString());
            writer.newLine();
            saveInOrder(node.right, writer);
        }
    }

    /**
     * Loads statements from a file into the knowledge base
     * @param fileName Name of the file to load
     * @throws IOException If file reading fails
     */
    private void loadFromFile(String fileName) throws IOException {
        timer.start();
        Thread loadingThread = ConsoleStyle.startLoading("Reading file");
        
        size = 0;
        root = null;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) {
                    insertStatement(statement);
                    count++;
                    if (count % 1000 == 0) {
                        System.out.print("\rProcessed " + count + " statements...");
                    }
                }
            }
        }

        timer.stop();
        ConsoleStyle.stopLoading(loadingThread);
        
        System.out.print("\r" + " ".repeat(50) + "\r");
        
        ConsoleStyle.printSuccess("Successfully loaded " + count + " statements.");
        ConsoleStyle.printPerformanceStats("Loading " + count + " statements", timer.getTimeInMilliseconds());
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        
        this.size = count;
    }

    /**
     * Checks if the knowledge base is empty
     * @return true if empty, false otherwise
     */
    private boolean isEmpty() {
        return root == null || size == 0;
    }

}
