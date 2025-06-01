# Cloud storage server and client

- The cloud storage `server` realisation is in the
  file [StorageServer](server/src/main/java/cloudstorage/server/StorageServer.java).

To run a server run [StorageServerApplication](server/src/main/java/cloudstorage/StorageServerApplication.java) with
the required server `port` specified in [server.properties](server/src/main/resources/cloudstorage/server.properties).

- The cloud storage `client` realisation is in the
  file [StorageClient](client/src/main/java/cloudstorage/client/StorageClient.java).

To start a client run [StorageClientApplication](client/src/main/java/cloudstorage/StorageClientApplication.java) with
the required server `host` and `port` specified
in [client.properties](client/src/main/resources/cloudstorage/client.properties).

## Available commands

- `register <login> <password> <password>` – register on the server
- `login <login> <password>` – login to the server
- `quit` – disconnect from the server
- `help` – print available commands with description