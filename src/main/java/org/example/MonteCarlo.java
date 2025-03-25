package org.example;

public class MonteCarlo {

    private int numSamples;
    private RandomGenerator random;
    private double piEstimate = 0.0;
    private long duration = 0;

    MonteCarlo(int numSamples, RandomGenerator random) {
        this.numSamples = numSamples;
        this.random = random;
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public double estimatePi() {
        long startTime = System.nanoTime();

        int insideCircle = 0;

        for (int i = 0; i < numSamples; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (getDistance(x, y, 0, 0) <= 1) {
                insideCircle++;
            }
        }
        duration= System.nanoTime() - startTime; // Duration in nanoseconds

        piEstimate = 4.0 * insideCircle / numSamples;
        return piEstimate;
    }

    public String toString() {
        return "Class: " + random.getClass() + "\n" +
                "Estimated Pi: " + piEstimate + "\n" +
                "Number of samples: " + numSamples + "\n" +
                "Duration: " + duration / 1000000.0 + " ms";
    }
}
