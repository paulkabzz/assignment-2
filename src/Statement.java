/**
 * Represents a statement in the knowledge base.
 * Each statement consists of a term, a sentence, and a confidence score.
 * Implements Comparable to allow sorting and comparison in the AVL tree.
 */
public class Statement implements Comparable<Statement> {
    private String term;
    private String sentence;
    private double confidenceScore;

    /**
     * Constructs a new Statement with the specified term, sentence, and confidence score.
     *
     * @param term The term or keyword for this statement
     * @param sentence The full sentence or description
     * @param confidenceScore The confidence score (typically between 0.0 and 1.0)
     */
    public Statement(String term, String sentence, double confidenceScore) {
        this.term = term;
        this.sentence = sentence;
        this.confidenceScore = confidenceScore;
    }

    /**
     * Creates a Statement object from a tab-delimited line of text.
     * Expected format: "term\tsentence\tconfidenceScore"
     *
     * @param line The tab-delimited line to parse
     * @return A new Statement object, or null if the line couldn't be parsed
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

    /**
     * Gets the term of this statement.
     *
     * @return The term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Gets the sentence of this statement.
     *
     * @return The sentence
     */
    public String getSentence() {
        return sentence;
    }

    /**
     * Gets the confidence score of this statement.
     *
     * @return The confidence score
     */
    public double getConfidenceScore() {
        return confidenceScore;
    }

    /**
     * Compares this statement with another statement based on their terms.
     *
     * @param other The statement to compare with
     * @return A negative integer, zero, or a positive integer as this statement's term
     *         is less than, equal to, or greater than the other statement's term
     */
    @Override
    public int compareTo(Statement other) {
        return this.term.compareTo(other.term);
    }

    /**
     * Returns a string representation of this statement in tab-delimited format.
     *
     * @return A string in the format "term\tsentence\tconfidenceScore"
     */
    @Override
    public String toString() {
        return term + "\t" + sentence + "\t" + confidenceScore;
    }
}