package cloudstorage.service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationService {
    private final Map<String, String> userIdentifiers = new ConcurrentHashMap<>();
    private final Set<String> authorizedUsers = ConcurrentHashMap.newKeySet();

    public boolean isUserRegistered(String login) {
        return userIdentifiers.containsKey(login);
    }

    public boolean isUserAuthorized(String login) {
        return !login.equals("unauthorized") && authorizedUsers.contains(login);
    }

    public void registerUser(String login, String password) {
        userIdentifiers.put(login, password);
    }

    public void authorizeUser(String login) {
        authorizedUsers.add(login);
    }

    public boolean identifiersMatch(String login, String password) {
        return Objects.equals(password, userIdentifiers.get(login));
    }
}
