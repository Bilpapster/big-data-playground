package storageProducerConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int storage;
    private final int serverId;
    private final int port;
    private final ServerSocket serverSocket;
    private boolean isActive;


    public Server(int serverId, int port) throws IOException {
        this.serverId = serverId;
        this.port = port;
        this.storage = new Random().nextInt(1000) + 1;
        this.serverSocket = new ServerSocket(port);
        int TIMEOUT_MILLIS = 60000;
        serverSocket.setSoTimeout(TIMEOUT_MILLIS);
        this.isActive = true;
        System.out.println("Server " + serverId + " started with initial storage: " + storage);
    }

    public Server(int serverId, int port, int timeout) throws IOException {
        this.serverId = serverId;
        this.port = port;
        this.storage = new Random().nextInt(1000) + 1;
        this.serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeout);
        System.out.println("Server " + serverId + " started with initial storage: " + storage);
    }

    public synchronized boolean addToStorage(int value) {
        if (storage + value > 1000) {
            System.out.println("Server " + serverId + ": Storage overflow! Value not added.");
            return false;
        }
        storage += value;
        return true;
    }

    public synchronized boolean removeFromStorage(int value) {
        if (storage - value < 1) {
            System.out.println("Server " + serverId + ": Storage underflow! Value not removed.");
            return false;
        }
        storage -= value;
        return true;
    }

    /*
     *  Begins threaded listening to client connections.
     *  If the TIMEOUT time is exceeded, server shuts down.
     */
    public void start() {
        ExecutorService pool = Executors.newCachedThreadPool();
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                pool.submit(() -> handleClient(clientSocket));
            } catch (SocketTimeoutException e) {
                System.out.println("Server " + serverId + ": No connection within timeout period. Shutting down with " +
                        "storage :" +
                        storage +
                        ".");
                shutdown();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *  Retains open connection with a client (Consumer/Producer) where handles its commands (ADD/REMOVE).
     *  Finally, returns a notifies the client with the result.
     */
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command = in.readLine();
            int value = Integer.parseInt(in.readLine());
            boolean result;

            if ("ADD".equals(command)) {
                result = addToStorage(value);
            } else if ("REMOVE".equals(command)) {
                result = removeFromStorage(value);
            } else {
                result = false;
            }

            out.println(result ? "SUCCESS" : "FAILURE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isActive() {
        return isActive;
    }

    private void shutdown() {
        try {
            serverSocket.close();
            isActive = false;
            System.out.println("Server " + serverId + " has been shut down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        // Default values
        int serverCount = 5;
        int hostPort = 9000;

        // Parse command-line arguments
        try {
            serverCount = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("Warning: missing serverCount; using default (5)");
        }
        try {
            hostPort = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Warning: missing serverPort; using default (9000)");
        }

        Server[] servers = new Server[serverCount];
        ExecutorService serverPool = Executors.newFixedThreadPool(serverCount);

        // Start all servers
        for (int i = 0; i < serverCount; i++) {
            servers[i] = new Server(i, hostPort + i);
            int finalI = i;
            serverPool.submit(() -> servers[finalI].start());
        }

        // Start all producers and consumers
        for (int i = 0; i < 10; i++) {
            new Producer(servers,i).start();
            new Consumer(servers,i).start();
        }


        // Monitoring servers; if all servers are down, terminate.
        while (true) {
            boolean allServersInactive = true;
            for (Server server : servers) {
                if (server.isActive()) {
                    allServersInactive = false;
                    break;
                }
            }

            if (allServersInactive) {
                System.out.println("All servers have shut down. Terminating program.");
                serverPool.shutdown();
                break;
            }

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
