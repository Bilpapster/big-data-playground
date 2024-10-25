package keyValueStoreServerClient;

public class DefaultValues {
    private static final int COMMUNICATION_PORT = 8899;
    private static final String SERVER_ADDRESS = "127.0.0.1"; // == localhost
    private static final int HASH_TABLE_CAPACITY = 2 ^ 20;

    /**
     * Returns the default communication port for the client-server application.
     * Use the same for the client and the server to avoid discrepancies.
     *
     * @return default communication port.
     */
    public static int getDefaultPort() {
        return COMMUNICATION_PORT;
    }

    /**
     * Returns the default server address of the server.
     *
     * @return the server's default IP address.
     */
    public static String getDefaultServerAddress() {
        return SERVER_ADDRESS;
    }

    /**
     * Returns the default capacity of the hash table initialized
     * and stored at the server side.
     *
     * @return the default initial capacity of the hash table.
     */
    public static int getDefaultHashTableCapacity() {
        return HASH_TABLE_CAPACITY;
    }

}
