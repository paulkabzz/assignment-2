/**
 * Represents a single statement in the knowledge base
 */
public class Statement implements Comparable<Statement> {
    private String term;
    private String sentence;
    private double confidenceScore;

    /**
     * Creates a new statement
     * @param term The term the statement is about
     * @param sentence The statement text
     * @param confidenceScore Confidence score between 0 and 1
     */
    public Statement(String term, String sentence, double confidenceScore) {
        this.term = term;
        this.sentence = sentence;
        this.confidenceScore = confidenceScore;
    }

    /**
     * Creates a Statement object from a line of text
     * @param line Tab-separated line containing term, sentence, and confidence score
     * @return New Statement object, or null if parsing fails
     */
    public static Statement fromLine(String line) {
        try {
            String[] parts = line.split("\t");
            if (parts.length >= 3) {
                String term = parts[0].trim();
                String sentence = parts[1].trim();
                double confidence = Double.parseDouble(parts[2].trim());
                return new Statement(term, sentence, confidence);
            } else {
                System.err.println("Invalid line format: " + line);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line + "\nError: " + e.getMessage());
        }
        return null;
    }

    public String getTerm() {
        return term;
    }

    public String getSentence() {
        return sentence;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    /**
     * Compares this statement with another based on term
     * @param other The statement to compare with
     * @return Negative if this term comes before, positive if after, 0 if equal
     */
    @Override
    public int compareTo(Statement other) {
        return this.term.compareTo(other.term);
    }

    @Override
    public String toString() {
        return term + "\t" + sentence + "\t" + confidenceScore;
    }
}