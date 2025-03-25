package org.example;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Main {

    public static void main(String[] args) {
        CommandLineOptions options = new CommandLineOptions();
        CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);

            if (options.isBenchmark()) {
                System.out.println("Running benchmark...\n");
                benchmark();
            } else if (options.getOutput() != null) {
                System.out.println("Output file: " + options.getOutput());
                writeRandomDataToFile(options.getOutput(), 1024);
            } else if (options.isStart()) {
                System.out.println("Starting daemon...");
                RandomServer.start();
            } else if (options.isStop()) {
                System.out.println("Stopping daemon...");
                RandomServer.stop();
            } else if (options.isRestart()) {
                System.out.println("Restarting daemon...");
                RandomServer.restart();
            } else if (options.isPipe()) {
                System.out.println("Piping random output to parent process...");
                pipeRandomOutput(System.out);
            } else {
                System.out.println("No options specified");
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private static void benchmark() {
        MonteCarlo first = new MonteCarlo(10000000, new JustEntropy());
        first.estimatePi();
        System.out.println(first);
        System.out.println();
        MonteCarlo second = new MonteCarlo(10000000, new ShittyRandom(JustEntropy.generateEntropy(16)));
        second.estimatePi();
        System.out.println(second);
    }

    public static void writeRandomDataToFile(String filePath, int size) {
        int bufferSize = 1024;
        int written = 0;

        ShittyRandom random = new ShittyRandom(JustEntropy.generateEntropy(16));
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            ByteBuffer randomData = ByteBuffer.allocate(bufferSize);
            while (written < size) {
                for(int i = 0; i < bufferSize; i += 4) {
                    randomData.putInt(random.nextInt());
                }
                randomData.flip();
                outputStream.write(randomData.array(), 0, Math.min(bufferSize, size - written));
                randomData.clear();
                written += randomData.limit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pipeRandomOutput(OutputStream parentOutputStream) {
        int bufferSize = 1024;
        try (BufferedOutputStream outputStream = new BufferedOutputStream(parentOutputStream)) {
            ShittyRandom random = new ShittyRandom(JustEntropy.generateEntropy(16));
            ByteBuffer randomData = ByteBuffer.allocate(bufferSize);

            while (!Thread.interrupted()) {
                randomData.clear();
                for (int i = 0; i < bufferSize; i += 4) {
                    randomData.putInt(random.nextInt());
                }
                randomData.flip();
                outputStream.write(randomData.array(), 0, randomData.limit());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}