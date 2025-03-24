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

    public int height(Node node) {
        return node == null ? 0 : node.height;
    }

    public Node rightRotate(Node node) {

        Node node1 = node.left;
        Node node2 = node1.right;

        node1.right = node;
        node.left = node2;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        node1.height = 1 + Math.max(height(node1.left), height(node1.right));

        return node1;
    }

    private int getChoice() {
        return this.scanner.nextInt();
    }

    public void loadMainFile(String file) throws IOException {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);

            }

        } catch (IOException e) {
            ConsoleStyle.printError("Error loading file: " + e.getMessage());
        }
        
    }

    private int balanaceFactor(Node node) {
        return node == null ? 0 : height(node.right) - height(node.left);
    }

    private void fixHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

}
