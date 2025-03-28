import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main entry point for the GenericsKB AVL Tree application.
 * This class handles command-line arguments and initializes the application.
 */
public class Main {
    /**
     * Main method that starts the application.
     * 
     * @param args Command line arguments:
     *             args[0] - Input file path (optional)
     *             args[1] - Query file path (optional)
     */
    public static void main(String[] args) {
        GenericsKbAVLApp app = new GenericsKbAVLApp();
        
        if (args.length >= 1) {
            String inputFile = args[0];
            String queryFile = args.length >= 2 ? args[1] : null;
            
            if (queryFile != null) {
                app.runApplication(inputFile, queryFile);
            } else {
                // If only input file is provided, prompt for query file
                System.out.println("Input file provided: " + inputFile);
                app.runApplication(inputFile, promptForQueryFile());
            }
        } else {
            // No command line arguments, use interactive mode
            app.runApplication();
        }
    }
    
    /**
     * Prompts the user for a query file path.
     * 
     * @return The query file path entered by the user or the default path
     */
    private static String promptForQueryFile() {
        System.out.print("Enter query file path (or press Enter for default 'src/GenericsKB-queries.txt'): ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String queryFile = reader.readLine().trim();
            return queryFile.isEmpty() ? "src/GenericsKB-queries.txt" : queryFile;
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            return "src/GenericsKB-queries.txt";
        }
    }
}
