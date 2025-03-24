/**
 * Utility class for measuring operation execution time
 */
public class Timer {
    private long startTime;
    private long endTime;

    /**
     * Starts the timer
     */
    public void start() {
        startTime = System.nanoTime();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        endTime = System.nanoTime();
    }

    /**
     * Gets the elapsed time in milliseconds
     * @return Elapsed time between start and stop
     */
    public double getTimeInMilliseconds() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Gets a formatted string of the elapsed time
     * @return Formatted time string with appropriate units
     */
    public String getFormattedTime() {
        double ms = getTimeInMilliseconds();
        if (ms < 1.0) {
            return String.format("%.3f microseconds", ms * 1000);
        } else if (ms < 1000.0) {
            return String.format("%.3f milliseconds", ms);
        } else {
            return String.format("%.3f seconds", ms / 1000);
        }
    }
}
