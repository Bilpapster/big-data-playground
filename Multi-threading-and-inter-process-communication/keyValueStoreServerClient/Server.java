package keyValueStoreServerClient;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {

    private static final int CAPACITY = DefaultValues.getDefaultHashTableCapacity();
    private static int PORT = DefaultValues.getDefaultPort(); // Default value. Can be changed via CLI arguments.
    private static final Map<Integer, Integer> data = new HashMap<>(CAPACITY);
    private static boolean stopServer = false;

    public static void main(String[] args) throws IOException {
        parseArgs(args);

        // initialize server-side socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("The server is up and running. Waiting for client to connect ...");

        // initialize client-side socket
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept from client failed.");
            e.printStackTrace();
            System.exit(2);
        }
        System.out.println("Client connected. Waiting for input ...");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // receive requests iteratively, until 0 is given as operation code from the client side
        String requestLine;
        while ((requestLine = in.readLine()) != null) {
            int[] requestTokens = splitInputString(requestLine);
            int operationCode = requestTokens[0];
            int key = requestTokens[1];
            int value = requestTokens[2];
            out.println(processRequest(operationCode, key, value));
            if (stopServer) {
                break;
            }
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
        System.out.println("Server terminated.");
    }

    private static void parseArgs(String[] args) {
        // port, capacity
        if (args.length == 0) {
            return;
        }
        PORT = Integer.parseInt(args[0]);
    }

    /**
     * Splits a request string that is in the form "operationCode key value" to
     * an array of 3 integers in the same order. Note that for some operation
     * codes (e.g., 2) not all tokens need to be specified. In those cases, the
     * returned value in these positions is a dummy default (-1).
     *
     * @param input a request string in the form "operationCode key value".
     * @return an array of three integers: [operationCode, key, value].
     */
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
                // operation code 0 means end of communication
                System.out.println("Received termination request. Shutting down ...");
                stopServer = true;
                break;
            case 1:
                // operation code 1 means insert ("key", "value") pair to the hash table
                data.put(key, value);
                System.out.println("Inserted pair (" + key + ", " + value + ")");
                return 1;
            case 2:
                // operation code 2 means delete the value for "key"
                data.remove(key);
                System.out.println("Deleted value for key=" + key);
                return 1;
            case 3:
                // operation code 3 means search for the value of "key"
                System.out.println("Searching for key=" + key);
                return data.getOrDefault(key, 0);
            // all other operation codes are ignored
        }
        return 0;
    }
}
