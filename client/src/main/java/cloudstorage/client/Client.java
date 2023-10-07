package cloudstorage.client;

/**
 * The client interface
 */
public interface Client {
    /**
     * Starts client's work.
     *
     * @throws Exception if an error occurred during client's working.
     */
    void start() throws Exception;
}
