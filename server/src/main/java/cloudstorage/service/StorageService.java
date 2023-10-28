package cloudstorage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageService {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    public void createUserDirectory(String user) throws IOException {
        Path userPath = getUserPath(user);
        if (Files.notExists(userPath)) {
            Files.createDirectory(userPath);
        }
    }

    public void storeFile(String user, String path, byte[] bytes) throws IOException {
        Path filePath = getUserPath(user, path);
        createMissingDirectories(filePath);
        Files.write(filePath, bytes);
    }

    public byte[] downloadFile(String user, String path) throws IOException {
        return Files.readAllBytes(getUserPath(user, path));
    }

    public void moveFile(String user, String from, String to) throws IOException {
        Path destinationPath = getUserPath(user, to);
        createMissingDirectories(destinationPath);
        Files.move(getUserPath(user, from), destinationPath);
    }

    private void createMissingDirectories(Path filePath) throws IOException {
        Path parentPath = filePath.getParent();
        if (parentPath != null && Files.notExists(parentPath)) {
            Files.createDirectories(parentPath);
        }
    }

    private Path getUserPath(String user) {
        return getUserPath(user, "");
    }

    private Path getUserPath(String user, String path) {
        return ROOT.resolve(user).resolve(path);
    }
}
