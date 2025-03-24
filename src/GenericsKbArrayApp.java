import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

/**
 * Array implementation of the GenericsKB knowledge base
 */
public class GenericsKbArrayApp {
    private Statement[] statements;
    private int size;
    private Scanner scanner;
    private Timer timer;

    /**
     * Constructs a new array-based knowledge base
     * @param scanner Scanner for user input
     */
    public GenericsKbArrayApp(Scanner scanner) {
        this.statements = new Statement[100000];
        this.size = 0;
        this.scanner = scanner;
        this.timer = new Timer();
        
        // Clear screen and show welcome message
        ConsoleStyle.clearScreen();
        ConsoleStyle.printAppWelcome("Array Implementation");
        
        Thread loadingThread = ConsoleStyle.startLoading("Loading Array application");
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
                    loadKnowledgeBase();
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
                    ConsoleStyle.printGoodbye();
                    running = false;
                    break;
                default:
                    ConsoleStyle.printError("Invalid choice. Please try again.");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
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
        System.out.println(ConsoleStyle.CYAN + "5. " + ConsoleStyle.RESET + "Quit");
        ConsoleStyle.printDivider();
    }

        /**
     * This function retrieves a user's choice from stdin.
     *
     * @return The integer value representing the user's choice.
     */
    private int getChoice() {
        ConsoleStyle.printPrompt("Enter your choice: ");
        return scanner.nextInt();
    }

    /**
     * Updates existing statement or adds new one
     * @param newStatement Statement to add or update
     */
    private void updateOrAddStatement(Statement newStatement) {
        for (int i = 0; i < size; i++) {
            if (statements[i].getTerm().equals(newStatement.getTerm())) {
                if (newStatement.getConfidenceScore() > statements[i].getConfidenceScore()) statements[i] = newStatement;
                return;
            }
        }

        if (size < statements.length) {
            statements[size++] = newStatement;
        } else {
            ConsoleStyle.printError("Knowledge base is full. Cannot add more statements.");
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
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) {
                    updateOrAddStatement(statement);
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
            e.printStackTrace();
        }
    }

     /**
     * Loads the knowledge base from a file specified by the user.
     */
    private void loadKnowledgeBase() {
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
            System.out.println("File size: " + file.length() + " bytes");
            
            loadFromFile(file.getPath());
            
            System.out.println("\nDebug Information:");
            System.out.println("Current size: " + size);
            System.out.println("Is knowledge base empty: " + isEmpty());
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            
        } catch (IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
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
            updateOrAddStatement(newStatement);
            
            ConsoleStyle.printSuccess("Statement for term '" + term + "' has been updated.");
        } catch (NumberFormatException e) {
            ConsoleStyle.printError("Invalid confidence score. Please enter a valid number.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Searches for a statement by term
     */
    private void searchByTerm() {
        scanner.nextLine(); 
        
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
        
        boolean found = false;
        for (int i = 0; i < size; i++) {
            if (statements[i].getTerm().equals(term)) {
                found = true;
                timer.stop();
                ConsoleStyle.stopLoading(loadingThread);
                
                System.out.println("\n" + ConsoleStyle.GREEN + "Statement found:" + ConsoleStyle.RESET);
                System.out.println(ConsoleStyle.YELLOW + "Term: " + ConsoleStyle.RESET + statements[i].getTerm());
                System.out.println(ConsoleStyle.YELLOW + "Statement: " + ConsoleStyle.RESET + statements[i].getSentence());
                System.out.println(ConsoleStyle.YELLOW + "Confidence: " + ConsoleStyle.RESET + statements[i].getConfidenceScore());
                break;
            }
        }
        
        if (!found) {
            timer.stop();
            ConsoleStyle.stopLoading(loadingThread);
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
        
        boolean found = false;
        for (int i = 0; i < size; i++) {
            if (statements[i].getTerm().equals(term) && 
                statements[i].getSentence().equals(sentence)) {
                found = true;
                timer.stop();
                ConsoleStyle.stopLoading(loadingThread);
                System.out.println("\nThe statement was found and has a confidence score of " + statements[i].getConfidenceScore() + ".");
                break;
            }
        }
        
        if (!found) {
            timer.stop();
            ConsoleStyle.stopLoading(loadingThread);
            ConsoleStyle.printError("Statement not found.");
        }
        
        ConsoleStyle.printPerformanceStats("Search operation", timer.getTimeInMilliseconds());
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Checks if the knowledge base is empty
     * @return true if empty, false otherwise
     */
    private boolean isEmpty() {
        return size == 0;
    }
}
