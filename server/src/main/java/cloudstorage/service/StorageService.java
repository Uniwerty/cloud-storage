package cloudstorage.service;

import io.netty.buffer.ByteBuf;
import io.netty.handler.stream.ChunkedFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StorageService {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    public void createUserDirectory(String user) throws IOException {
        Path userPath = getUserPath(user);
        if (Files.notExists(userPath)) {
            Files.createDirectory(userPath);
        }
    }

    public int storeFileChunk(String user, String path, ByteBuf fileChunk) throws IOException {
        Path filePath = getUserPath(user, path);
        createMissingDirectories(filePath);
        try (OutputStream fileOutput =
                     Files.newOutputStream(
                             filePath,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.APPEND
                     )
        ) {
            int readableBytes = fileChunk.readableBytes();
            fileChunk.readBytes(fileOutput, readableBytes);
            return readableBytes;
        }
    }

    public ChunkedFile getChunkedFile(String user, String path) throws IOException {
        return new ChunkedFile(getUserPath(user, path).toFile());
    }

    public void moveFile(String user, String from, String to) throws IOException {
        Path destinationPath = getUserPath(user, to);
        createMissingDirectories(destinationPath);
        Files.move(getUserPath(user, from), destinationPath);
    }

    public long getFileSize(String user, String path) throws IOException {
        return Files.size(getUserPath(user, path));
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
