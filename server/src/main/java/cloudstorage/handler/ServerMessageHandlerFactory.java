package cloudstorage.handler;

/**
 * The factory class providing {@link ServerMessageHandler} implementations
 */
public class ServerMessageHandlerFactory {
    /**
     * Provides a {@link StorageServerMessageHandler} instance.
     *
     * @return a new {@link StorageServerMessageHandler} instance
     */
    public static ServerMessageHandler storageServerMessageHandler() {
        return new StorageServerMessageHandler();
    }
}
