package cloudstorage.handler;

/**
 * The factory class providing {@link ClientMessageHandler} implementations
 */
public class ClientMessageHandlerFactory {
    /**
     * Provides a {@link StorageClientMessageHandler} instance.
     *
     * @return a new {@link StorageClientMessageHandler} instance
     */
    public static ClientMessageHandler storageClientMessageHandler() {
        return new StorageClientMessageHandler();
    }
}
