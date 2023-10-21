package cloudstorage.handler;

public class ClientMessageHandlerFactory {
    public static ClientMessageHandler storageClientMessageHandler() {
        return new StorageClientMessageHandler();
    }
}
