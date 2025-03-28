public class Statement implements Comparable<Statement> {
    private String term;
    private String sentence;
    private double confidenceScore;

    public Statement(String term, String sentence, double confidenceScore) {
        this.term = term;
        this.sentence = sentence;
        this.confidenceScore = confidenceScore;
    }

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

    @Override
    public int compareTo(Statement other) {
        return this.term.compareTo(other.term);
    }

    @Override
    public String toString() {
        return term + "\t" + sentence + "\t" + confidenceScore;
    }
}