/**
 * Utility class for console styling and formatting
 * 
 */
public class ConsoleStyle {
    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    
    // Styling
    public static final String BOLD = "\u001B[1m";
    
    private static final String[] LOADING_FRAMES = new String[] {
        "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"
    };

    private static volatile boolean isLoading = false;

    /**
     * Clears the console screen
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Prints the main application header
     */
    public static void printHeader() {
        System.out.println(PURPLE + 
            "╔═══════════════════════════════════════════════════════════════╗\n" +
            "║                     GenericsKB Knowledge Base                 ║\n" +
            "║                     =========================                 ║\n" +
            "╚═══════════════════════════════════════════════════════════════╝" + 
            RESET);
    }
    
    /**
     * Prints a success message in green
     * @param message The message to display
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }
    
    /**
     * Prints an error message in red
     * @param message The message to display
     */
    public static void printError(String message) {
        System.out.println(RED + "✘ " + message + RESET);
    }
    
    /**
     * Prints a prompt message in cyan
     * @param message The message to display
     */
    public static void printPrompt(String message) {
        System.out.print(CYAN + "➜ " + message + RESET);
    }
    
    public static void printDivider() {
        System.out.println(BLUE + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" + RESET);
    }

    /**
     * Starts a loading animation with a message.
     * 
     * @author Paul Kabulu
     * Credit to Joel chigumba - I used Joel Chigumba's implementation to help me with this method, his github is https://github.com/joesome-git.    
     * 
     * @param message The message to display during loading
     * @return The thread running the animation
     */
    public static Thread startLoading(String message) {
        isLoading = true;
        Thread loadingThread = new Thread(() -> {
            int i = 0;
            while (isLoading) {
                System.out.print("\r" + CYAN + LOADING_FRAMES[i] + " " + message + RESET);
                i = (i + 1) % LOADING_FRAMES.length;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.print("\r" + " ".repeat(message.length() + 2) + "\r");
        });
        loadingThread.start();
        return loadingThread;
    }

    /**
     * Stops the loading animation
     * @param loadingThread The thread running the animation
     */
    public static void stopLoading(Thread loadingThread) {
        isLoading = false;
        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            System.out.println();
        }
    }

    public static void printPerformanceStats(String operation, double timeInMs) {
        System.out.println(BLUE + "⚡ Performance: " + operation + " took " + 
            String.format("%.3f ms", timeInMs) + RESET);
    }

    public static void printAppWelcome(String appType) {
        clearScreen();
        System.out.println(PURPLE + 
            "╔═══════════════════════════════════════════════════════════════╗\n" +
            "║                 Welcome to GenericsKB " + appType + "         ║\n" +
            "╚═══════════════════════════════════════════════════════════════╝" + 
            RESET + "\n");
    }

    /**
     * Prints the goodbye message when exiting the application
     */
    public static void printGoodbye() {
        clearScreen();
        System.out.println(PURPLE + 
            "╔═══════════════════════════════════════════════════════════════╗\n" +
            "║                   Thank you for using GenericsKB              ║\n" +
            "║                        Have a great day!                      ║\n" +
            "╚═══════════════════════════════════════════════════════════════╝" + 
            RESET);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }
} 