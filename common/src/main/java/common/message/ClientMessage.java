package common.message;

public record ClientMessage(String name, String[] arguments, long fileSize) {
    public ClientMessage(String name, String[] arguments) {
        this(name, arguments, 0);
    }
}
