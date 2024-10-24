package keyValueStoreServerClient;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {

    private static final int CAPACITY = 2 ^ 20;
    private static final Map<Integer, Integer> data = new HashMap<>(CAPACITY);
    private static final Random random = new Random();
    private static boolean stopServer = false;

    public static void main(String[] args) throws IOException {

        initializeData();

        ServerSocket serverSocket = null;
        int port = 8888; // todo read args

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(1);
        }

        Socket clientSocket = null;

        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept from client failed.");
            System.exit(2);
        }

        System.out.println("Connection successful ...");
        System.out.println("Waiting for input ...");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            int[] arguments = splitInputString(inputLine);
            int operationCode = arguments[0];
            int key = arguments[1];
            int value = arguments[2];
            out.println(processRequest(operationCode, key, value));
            if (stopServer) {
                break;
            }
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    private static void initializeData() {
        // initialize hash table
        for (int i = 0; i < CAPACITY; i++) {
            int key = random.nextInt(CAPACITY);
            int value = random.nextInt(CAPACITY);
            data.put(key, value);
        }
    }

    private static int[] splitInputString(String input) {
        String[] tokens = input.split(" ");
        int DUMMY = -1;
        int key = DUMMY;
        int value = DUMMY;
        int operationCode = Integer.parseInt(tokens[0]);
        switch (tokens.length) {
            case 1:
                break;
            case 2:
                key = Integer.parseInt(tokens[1]);
                break;
            default:
                key = Integer.parseInt(tokens[1]);
                value = Integer.parseInt(tokens[2]);
                break;
        }
        return new int[]{operationCode, key, value};
    }

    private synchronized static int processRequest(int operationCode, int key, int value) {
        switch (operationCode) {
            case 0:
                stopServer = true;
                break;
            case 1:
                data.put(key, value);
                return 1;
            case 2:
                data.remove(key);
                return 1;
            case 3:
                return data.getOrDefault(key, 0);
        }
        return 0;
    }
}
