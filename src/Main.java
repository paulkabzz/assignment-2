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
            String queryFile = args.length >= 2 ? args[1] : "src/GenericsKB-queries.txt";
            app.runApplication(inputFile, queryFile);
        } else {
            app.runApplication("src/GenericsKB.txt", "src/GenericsKB-queries.txt");
        }
    }
}
