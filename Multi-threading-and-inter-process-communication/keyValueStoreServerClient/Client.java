package keyValueStoreServerClient;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class Client {
    private static String serverAddress = DefaultValues.getDefaultServerAddress();
    private static int port = DefaultValues.getDefaultPort();
    private static String fileName = "keyValueStoreServerClient/requests.txt";

    public static void main(String[] args) throws IOException {
        parseArgs(args);

        System.out.println("Attempting to connect to host " + serverAddress);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            echoSocket = new Socket(serverAddress, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverAddress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + serverAddress);
            System.exit(2);
        }

        System.out.println("Successfully connected to the server. Starting sending requests from file " + fileName);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String requestString;
        while ((requestString = stdIn.readLine()) != null) {
            System.out.println("Me: " + requestString);
            out.println(requestString);
            System.out.println("Response from Server: " + in.readLine());
            System.out.println();
        }
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    private static void parseArgs(String[] args) {
        // args must be provided in the form "filename port ip" where both are optional but must respect this ordering
        int numberOfArgs = args.length;
        if (numberOfArgs == 0) return;

        fileName = args[0];
        if (numberOfArgs == 1) return;

        port = Integer.parseInt(args[1]);
        if (numberOfArgs == 2) return;

        serverAddress = args[2];
    }
}
