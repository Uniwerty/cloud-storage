package cloudstorage;

import cloudstorage.server.StorageServer;

import java.io.InputStream;
import java.util.Properties;

public class StorageServerApplication {
    private static final String PROPERTIES_FILE = "server.properties";

    public static void main(String[] args) {
        StorageServer server;
        try (InputStream propertiesStream =
                     StorageServerApplication.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (propertiesStream == null) {
                StorageServer.logError("Property file was not found");
                return;
            }
            Properties properties = new Properties();
            properties.load(propertiesStream);

            server = new StorageServer(Integer.parseInt(properties.getProperty("port")));
            server.run();
        } catch (Exception e) {
            StorageServer.logError(e.getMessage());
        }
    }
}
