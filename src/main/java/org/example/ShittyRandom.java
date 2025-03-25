package org.example;

import java.nio.ByteBuffer;

public class ShittyRandom implements RandomGenerator {
    private long state0;
    private long state1;

    public ShittyRandom(ByteBuffer seed) {
        state0 = 0xCA35CA35CA35CA35L ^ seed.getLong();
        state1 = 0xCA53CA53CA53CA53L ^ seed.getLong();
    }

    public int nextInt() {
        long temp = ~state0 * -5;
        state0 = ~state1 * -13;
        state1 = Long.rotateLeft(temp, 32);
        return (int) (state0 ^ state1);
    }
}
