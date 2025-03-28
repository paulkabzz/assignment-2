import java.io.*;
import java.util.*;

/**
 * Class to automate experiments for measuring AVL tree performance
 * with different dataset sizes
 */
public class ExperimentRunner {
    // Dataset sizes to test (increasing by factor of ~10)
    private static final int[] DATASET_SIZES = {5, 10, 50, 100, 500, 1000, 5000, 10000, 25000, 50000};
    private static final String RESULTS_FILE = "experiment_results.csv";
    private static final String QUERY_FILE = "src/GenericsKB-queries.txt";
    private static final String FULL_DATASET_FILE = "src/GenericsKB.txt";
    
    private List<Statement> allStatements;
    private String[] queryTerms;
    
    /**
     * Main method to run the experiment
     */
    public static void main(String[] args) {
        ExperimentRunner runner = new ExperimentRunner();
        runner.runExperiment();
    }
    
    /**
     * Constructor - loads the full dataset and query terms
     */
    public ExperimentRunner() {
        allStatements = new ArrayList<>();
        loadFullDataset();
        loadQueryTerms();
    }
    
    /**
     * Runs the complete experiment for all dataset sizes
     */
    public void runExperiment() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE))) {
            // Write CSV header
            writer.println("Size,InsertMin,InsertAvg,InsertMax,SearchMin,SearchAvg,SearchMax");
            
            for (int size : DATASET_SIZES) {
                System.out.println("Running experiment with dataset size: " + size);
                
                // Create randomized subset
                List<Statement> subset = createRandomSubset(size);
                
                // Save subset to temporary file
                String tempFile = "temp_dataset_" + size + ".txt";
                saveSubsetToFile(subset, tempFile);
                
                // Run experiment with this subset
                ExperimentResult result = runSingleExperiment(tempFile);
                
                // Write results to CSV
                writer.println(size + "," + 
                               result.insertMin + "," + 
                               result.insertAvg + "," + 
                               result.insertMax + "," + 
                               result.searchMin + "," + 
                               result.searchAvg + "," + 
                               result.searchMax);
                
                // Clean up temp file
                new File(tempFile).delete();
            }
            
            System.out.println("Experiment completed. Results saved to " + RESULTS_FILE);
        } catch (IOException e) {
            System.err.println("Error running experiment: " + e.getMessage());
        }
    }
    
    /**
     * Loads the full dataset from file
     */
    private void loadFullDataset() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FULL_DATASET_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Statement statement = Statement.fromLine(line);
                if (statement != null) {
                    allStatements.add(statement);
                }
            }
            System.out.println("Loaded " + allStatements.size() + " statements from full dataset");
        } catch (IOException e) {
            System.err.println("Error loading full dataset: " + e.getMessage());
        }
    }
    
    /**
     * Loads query terms from file
     */
    private void loadQueryTerms() {
        List<String> terms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(QUERY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                terms.add(line.trim());
            }
            queryTerms = terms.toArray(new String[0]);
            System.out.println("Loaded " + queryTerms.length + " query terms");
        } catch (IOException e) {
            System.err.println("Error loading query terms: " + e.getMessage());
        }
    }
    
    /**
     * Creates a randomized subset of the full dataset
     */
    private List<Statement> createRandomSubset(int size) {
        List<Statement> subset = new ArrayList<>();
        List<Statement> tempList = new ArrayList<>(allStatements);
        Collections.shuffle(tempList);
        
        for (int i = 0; i < Math.min(size, tempList.size()); i++) {
            subset.add(tempList.get(i));
        }
        
        return subset;
    }
    
    /**
     * Saves a subset of statements to a file
     */
    private void saveSubsetToFile(List<Statement> statements, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Statement statement : statements) {
                writer.write(statement.toString());
                writer.newLine();
            }
        }
    }
    
    /**
     * Runs a single experiment with the given dataset file
     */
    private ExperimentResult runSingleExperiment(String datasetFile) {
        InstrumentedAVLTree tree = new InstrumentedAVLTree();
        
        // Load dataset and track insert comparisons
        List<Long> insertComparisons = tree.loadFromFile(datasetFile);
        
        // Run queries and track search comparisons
        List<Long> searchComparisons = tree.searchTerms(queryTerms);
        
        // Calculate statistics
        ExperimentResult result = new ExperimentResult();
        
        // Insert stats
        result.insertMin = Collections.min(insertComparisons);
        result.insertMax = Collections.max(insertComparisons);
        result.insertAvg = calculateAverage(insertComparisons);
        
        // Search stats
        result.searchMin = Collections.min(searchComparisons);
        result.searchMax = Collections.max(searchComparisons);
        result.searchAvg = calculateAverage(searchComparisons);
        
        return result;
    }
    
    /**
     * Calculates the average of a list of values
     */
    private double calculateAverage(List<Long> values) {
        long sum = 0;
        for (Long value : values) {
            sum += value;
        }
        return (double) sum / values.size();
    }
    
    /**
     * Class to hold experiment results
     */
    private static class ExperimentResult {
        long insertMin;
        long insertMax;
        double insertAvg;
        long searchMin;
        long searchMax;
        double searchAvg;
    }
}