import java.io.*;
import java.net.*;

public class Server {
    public static void main( String args[] ) {
        int maxClients = 2;
        int activeClients = 0;

        // Declare a server & client socket
        ServerSocket service = null;
        Socket server = null;

        ClientHandler[] handlers = new ClientHandler[maxClients];

        // Open server socket on port 5000
        try {
            service = new ServerSocket(5000);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Create socket object to listen/ accept connections
        try {
            do {
                server = service.accept();

                ClientHandler handler = new ClientHandler(server);

                Thread t = new Thread(handler);
                t.start();

                handlers[activeClients] = handler;

                activeClients++;

                if (activeClients == maxClients) {
                    break;
                }
            } while (true);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        while (true) {
            // Keep server open/ alive as long as there is a client connection
            boolean allClientsAreActive = false;

            for (int i=0; i < activeClients; i++) {
                ClientHandler handler = handlers[i];

                if (handler.isAlive()) {
                    allClientsAreActive = true;
                    break;
                }

                if (!allClientsAreActive) {
                    break;
                }

                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}