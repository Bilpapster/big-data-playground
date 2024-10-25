package storageProducerConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Consumer extends Thread{
    private final Server[] servers;
    private final Random random = new Random();
    private final int id;
    private final long runTime;

    public Consumer(Server[] servers, int id) {
        this.servers = servers;
        this.id = id;
        this.runTime = 60000;
    }

    public Consumer(Server[] servers, int id, long runTimeMilliseconds) {
        this.servers = servers;
        this.id = id;
        this.runTime = runTimeMilliseconds;
    }

    /**
     * Runs a determined duration, sending command-messages to a server within a 1-10 time space.
     * For each command, prints out the result.
     */
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < this.runTime) {
            try {
                int serverIndex = random.nextInt(servers.length);
                int valueToRemove = random.nextInt(91) + 10;
                try (Socket socket = new Socket("localhost", servers[serverIndex].getPort());
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    out.println("REMOVE");
                    out.println(valueToRemove);
                    String response = in.readLine();
                    System.out.println("Consumer " + this.id + ": " + response + " on server " + serverIndex);
                }

                Thread.sleep((random.nextInt(10) + 1) * 1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
