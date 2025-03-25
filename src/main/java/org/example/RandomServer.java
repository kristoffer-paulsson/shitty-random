package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomServer {

    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 1024;
    private static volatile boolean running = true;
    private static final Logger logger = Logger.getLogger(RandomServer.class.getName());
    private static final String CONTROL_FILE = "daemon.control";

    public static void start() {
        if (isRunning()) {
            System.out.println("Daemon is already running.");
            return;
        }
        new Thread(RandomServer::daemon).start();
    }

    public static void stop() {
        if (!isRunning()) {
            System.out.println("Daemon is not running.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONTROL_FILE))) {
            writer.write("STOP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void restart() {
        stop();
        while (isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        start();
    }

    private static boolean isRunning() {
        return new File(CONTROL_FILE).exists();
    }

    private static void daemon() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("RandomDaemon is running on port " + PORT);
            new File(CONTROL_FILE).createNewFile();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutdown signal received. Stopping daemon...");
                running = false;
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error closing server socket", e);
                }
                new File(CONTROL_FILE).delete();
            }));

            while (running) {
                if (new File(CONTROL_FILE).exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(CONTROL_FILE))) {
                        if ("STOP".equals(reader.readLine())) {
                            running = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        logger.log(Level.SEVERE, "Error accepting client connection", e);
                    }
                }
            }

            logger.info("Daemon stopped.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error starting server", e);
        } finally {
            new File(CONTROL_FILE).delete();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            long totalDataServed = 0;
            try (BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream())) {
                ShittyRandom random = new ShittyRandom(JustEntropy.generateEntropy(16));
                ByteBuffer randomData = ByteBuffer.allocate(BUFFER_SIZE);

                while (running && !clientSocket.isClosed()) {
                    randomData.clear();
                    for (int i = 0; i < BUFFER_SIZE; i += 4) {
                        randomData.putInt(random.nextInt());
                    }
                    randomData.flip();
                    outputStream.write(randomData.array(), 0, randomData.limit());
                    outputStream.flush();
                    totalDataServed += randomData.limit();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error handling client connection", e);
            } finally {
                try {
                    clientSocket.close();
                    logger.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
                    logger.info("Total data served to client: " + totalDataServed + " bytes");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error closing client socket", e);
                }
            }
        }
    }
}