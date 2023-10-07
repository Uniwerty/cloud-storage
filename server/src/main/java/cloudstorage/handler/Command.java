package cloudstorage.handler;

/**
 * The server commands enumeration
 */
public enum Command {
    REGISTER(3, "<login> <password> <password>", "register a new account"),
    LOGIN(2, "<login> <password>", "login to the server"),
    QUIT(0, "", "disconnect from the server"),
    HELP(0, "", "see available commands");


    private final int argumentsNumber;
    private final String arguments;
    private final String operation;

    Command(int argumentsNumber, String arguments, String operation) {
        this.argumentsNumber = argumentsNumber;
        this.arguments = arguments;
        this.operation = operation;
    }

    public String getName() {
        return toString().toLowerCase();
    }

    public int getArgumentsNumber() {
        return argumentsNumber;
    }

    public String getArguments() {
        return arguments;
    }

    public String getUsage() {
        return String.format("  %s %s - %s", getName(), arguments, operation);
    }
}
