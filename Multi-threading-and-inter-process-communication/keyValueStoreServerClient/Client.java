package keyValueStoreServerClient;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String serverHostName = "127.0.0.1"; // localhost todo read args
        int port = 8888; // todo read args
        if (args.length > 0) {
            serverHostName = args[0];
        }
        System.out.println("Attempting to connect to host " + serverHostName);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHostName, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + serverHostName);
            System.exit(2);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;

        System.out.println("Type Message (\"Bye.\" to quit):");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            if (userInput.equals("Bye."))
                break;
            System.out.println("echo: " + in.readLine());
            System.out.print("next message: ");
        }

        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }
}
