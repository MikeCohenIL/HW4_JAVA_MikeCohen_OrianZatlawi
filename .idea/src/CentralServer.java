import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The CentralServer class acts as the main hub of the Order Management System.
 * It listens for incoming client connections and manages a synchronized database
 * of business clients and their orders.
 */
public class CentralServer {
    /** The port number on which the server listens for connections. */
    private static final int PORT = 9999;
    /** A list acting as an in-memory database of all business clients. */
    private static final  List<BusinessClient> clientDatabase = new ArrayList<>();

    /**
     * The main method that starts the server.
     * It creates a ServerSocket and enters an infinite loop to accept new clients,
     * spinning off a new {@link ClientHandler} thread for each connection.
     * * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
    try(ServerSocket serverSocket = new ServerSocket(PORT)){
        System.out.print("Server listening on port " + PORT + "\n");
        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected:" + clientSocket.getInetAddress());
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }catch (IOException e){
        System.err.println(e.getMessage());
    }
}
    /**
     * Processes an incoming order by either updating an existing client
     * or creating a new one.
     * * <p>This method is <b>synchronized</b> to prevent race conditions,
     * ensuring that only one thread can modify the client database at a time.</p>
     * * @param name     The business name provided by the client.
     * @param id       The unique business ID.
     * @param itemType The category of the item being ordered.
     * @param quantity The amount of items to add.
     * @return         Response code: 100 (Success), 201 (Name mismatch for existing ID).
     */
public static synchronized int processOrder(String name , int id, int itemType ,int quantity){
    for(int i =0; i<clientDatabase.size(); i++){
        BusinessClient client = clientDatabase.get(i);
        if(client.getBusinessId() == id){
            if(!client.getName().equals(name)){
                return 201;
            }
            client.updateItems(itemType,quantity);
            return 100;
        }
    }
    BusinessClient newClient = new BusinessClient(name , id);
    newClient.updateItems(itemType,quantity);
    clientDatabase.add(newClient);
    return 100;
}
}