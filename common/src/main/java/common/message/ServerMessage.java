package common.message;

public record ServerMessage(boolean success, String command, String message, long fileSize) {
    public ServerMessage(boolean success, String command, String message) {
        this(success, command, message, 0);
    }
}
