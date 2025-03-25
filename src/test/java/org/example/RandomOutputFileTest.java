package org.example;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomOutputFileTest {

    @Test
    public void testWriteRandomDataToFile() throws IOException {
        String filePath = "testRandomData.bin";
        int size = 1024;

        // Call the method to write random data to the file
        Main.writeRandomDataToFile(filePath, size);

        // Verify the file exists
        File file = new File(filePath);
        assertTrue(file.exists(), "The file should exist");

        // Verify the file size
        assertTrue(file.length() == size, "The file size should be " + size);

        // Verify the file contains random data
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[size];
            int bytesRead = fis.read(data);
            assertTrue(bytesRead == size, "The number of bytes read should be " + size);

            ByteBuffer buffer = ByteBuffer.wrap(data);
            boolean hasRandomData = false;
            while (buffer.remaining() >= 4) {
                int randomValue = buffer.getInt();
                if (randomValue != 0) {
                    hasRandomData = true;
                    break;
                }
            }
            assertTrue(hasRandomData, "The file should contain random data");
        }

        // Clean up the test file
        file.delete();
    }
}