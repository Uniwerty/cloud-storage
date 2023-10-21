package cloudstorage;

import cloudstorage.client.StorageClient;

import java.io.InputStream;
import java.util.Properties;

public class StorageClientApplication {
    private static final String PROPERTIES_FILE = "client.properties";

    public static void main(String[] args) {
        StorageClient client;
        try (InputStream propertiesStream =
                     StorageClientApplication.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (propertiesStream == null) {
                StorageClient.logError("Property file was not found");
                return;
            }
            Properties properties = new Properties();
            properties.load(propertiesStream);

            client = new StorageClient(
                    properties.getProperty("serverHost"),
                    Integer.parseInt(properties.getProperty("serverPort"))
            );
            client.start();
        } catch (Exception e) {
            StorageClient.logError(e.getMessage());
        }
    }
}
