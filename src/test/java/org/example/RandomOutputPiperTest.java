package org.example;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomOutputPiperTest {

    @Test
    public void testPipeRandomOutput() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thread pipeThread = new Thread(() -> Main.pipeRandomOutput(outputStream));
        pipeThread.start();

        // Allow some time for the thread to generate output
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrupt the thread to stop the output generation
        pipeThread.interrupt();
        try {
            pipeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] outputData = outputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(outputData);

        // Check if the output contains random data
        boolean hasRandomData = false;
        while (buffer.remaining() >= 4) {
            int randomValue = buffer.getInt();
            if (randomValue != 0) {
                hasRandomData = true;
                break;
            }
        }

        assertTrue(hasRandomData, "The output should contain random data");
    }
}