package cloudstorage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageService {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    public void createUserDirectory(String user) throws IOException {
        Files.createDirectory(getUserPath(user));
    }

    private Path getUserPath(String user) {
        return getUserPath(user, "");
    }

    private Path getUserPath(String user, String path) {
        return ROOT.resolve(user).resolve(path);
    }
}
