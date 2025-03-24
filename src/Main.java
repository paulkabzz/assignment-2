// import java.util.*;
// import java.io.IOException;
// import java.util.*;
import java.util.Scanner;

/**
 * Main entry point for the GenericsKB application
 */
public class Main {
    /**
     * Main method that starts the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        ConsoleStyle.clearScreen();
        ConsoleStyle.printHeader();
        
        System.out.println(ConsoleStyle.YELLOW + "Welcome to the GenericsKB Application" + ConsoleStyle.RESET);
        System.out.println("\nChoose which implementation to use:");
        System.out.println(ConsoleStyle.CYAN + "1. " + ConsoleStyle.RESET + "Array-based implementation");
        System.out.println(ConsoleStyle.CYAN + "2. " + ConsoleStyle.RESET + "Binary Search Tree implementation");
        ConsoleStyle.printPrompt("Enter your choice: ");
        
        // int choice = scanner.nextInt();
        
        //  a small pause before transitioning
       
        
        scanner.close();
    }
}
