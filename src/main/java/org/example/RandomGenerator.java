package org.example;

public interface RandomGenerator {
    public int nextInt();

    public default double nextDouble() {
        return (nextInt() / ((double) (1 << 31)));
    }
}
