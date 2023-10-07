# Cloud storage server and client

- The cloud storage `server` realisation is in the file [StorageServer](server/src/main/java/cloudstorage/server/StorageServer.java).

To run a server run it with the required server `port` as an argument.

- The cloud storage `client` realisation is in the file [StorageClient](client/src/main/java/cloudstorage/client/StorageClient.java).

To start a client run it with the required server `host` and `port` as arguments.

## Available commands

- `register <login> <password> <password>` – register on the server
- `login <login> <password>` – login to the server
- `quit` – disconnect from the server
- `help` – print available commands with description