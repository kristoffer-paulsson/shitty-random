package org.example;

import java.nio.ByteBuffer;

import static java.lang.System.nanoTime;

public class JustEntropy implements RandomGenerator {

    private ByteBuffer seed;

    @Override
    public int nextInt() {
        if(seed == null || !seed.hasRemaining()) {
            seed = generateEntropy(1024);
        }
        return seed.getInt();
    }

    static public ByteBuffer generateEntropy(int size) {
        byte[] entropy = new byte[size];
        long start = nanoTime();
        long random = 0x3AC53AC53AC53AC5L * start;
        for (int i = 0; i < size; i++) {
            random = Long.rotateLeft((~random * -13) ^ (nanoTime() - start), 32);
            entropy[i] = (byte) random;
        }
        return ByteBuffer.wrap(entropy);
    }
}
