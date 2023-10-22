package cloudstorage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageService {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    public void createUserDirectory(String user) throws IOException {
        Files.createDirectory(getUserPath(user));
    }

    public void moveFile(String user, String from, String to) throws IOException {
        Files.move(getUserPath(user, from), getUserPath(user, to));
    }

    private Path getUserPath(String user) {
        return getUserPath(user, "");
    }

    private Path getUserPath(String user, String path) {
        return ROOT.resolve(user).resolve(path);
    }
}
