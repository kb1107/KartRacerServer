import java.io.*;
import java.net.*;

public class Client {
    // Declare client socket
    private static Socket clientSocket = null;

    // Declare output stream to send to server
    private static DataOutputStream outputStream = null;

    // Declare input stream from server
    private static BufferedReader inputStream = null;
    private static String responseLine; // holds input received from server

    // Server port: 5000
    private static String serverHost = "localhost";

    private static Kart ownKart = null;
    private static Kart foreignKart = null;

    private static String kartType; // Either "red" or "blue"

    private static Window window;

    public static void main( String[] args ) {
        String errorMessage = "Kart type needs to be defined as either 'red' or 'blue'.";

        if (args.length == 1) {
            kartType = args[0];

            if (!kartType.equals("blue") && !kartType.equals("red")) {
                System.err.println(errorMessage);
                return;
            }
        }
        else {
            System.err.println(errorMessage);
            return;
        }

        // Create a socket on port 5000 and open input and output streams
        try {
            clientSocket = new Socket(serverHost, 5000);

            outputStream = new DataOutputStream(
                    clientSocket.getOutputStream()
            );

            inputStream = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHost);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + serverHost);
        }

        // Write data to the socket
        if (clientSocket != null && outputStream != null && inputStream != null) {

            try {
                initialise();

                do {
                    responseLine = receiveMessage();

                    if (responseLine != null) {
                        System.out.println("SERVER: " + responseLine);

                        handleServerResponse(responseLine);
                    }

                    if (responseLine.equals("CLOSE")) {
                        shutdownClient();
                        break;
                    }
                } while (true);

                // close the input/output streams and socket
                outputStream.close();
                inputStream.close();
                clientSocket.close();
            }
            catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e.getMessage());
            }
            catch (IOException e) {
                System.err.println("IOException:  " + e.getMessage());
            }
        }
    }

    public static void initialise() {
        // initialise our client's own kart object
        ownKart = new Kart(kartType);

        sendMessage("identify " + kartType);

        sendMessage("ping");

        window = new Window();
        window.setVisible(true);
    }

    public static void sendOwnKart() {

        // direction, locationX, locationY, lapsLeft, crashFlag
        sendMessage("own_kart_update " +
                ownKart.getDirection() + " " +
                ownKart.getLocationX() + " " +
                ownKart.getLocationY() + " " +
                ownKart.getLapsLeft() + " " +
                ownKart.getCrashFlag()
        );
    }

    private static String receiveMessage() {
        try {
            return inputStream.readLine();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static void sendMessage(String message) {
        try {
            outputStream.writeBytes(message + "\n");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleServerResponse(String response) {
        String[] responseParts = response.split(" ");

        switch (responseParts[0]) {
            case "pong":

                try {
                    Thread.sleep(1);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                sendMessage("ping");

                break;

            case "foreign_kart_update":

                if (foreignKart == null) {
                    switch (kartType) {
                        case "red":
                            foreignKart = new Kart("blue");
                            break;
                        case "blue":
                            foreignKart = new Kart("red");
                            break;
                    }
                }

                //direction, locationX, locationY, lapsLeft, crashFlag
                foreignKart.updateDirection(Integer.parseInt(responseParts[1]));
                foreignKart.setLocationX(Double.parseDouble(responseParts[2]));
                foreignKart.setLocationY(Double.parseDouble(responseParts[3]));
                foreignKart.setLapsLeft(Integer.parseInt(responseParts[4]));
                foreignKart.setCrashFlag(Boolean.valueOf(responseParts[5]));

                break;

            case "player_exit":

                foreignKart = null;
                window.getPanel().notifyOpponentExit();

                break;
        }
    }

    public static void shutdownClient() {
        // shutdown script
        sendMessage("player_exit");

    }

    public static void detectKartCollision() {
        int redX = (int)ownKart.getLocationX();
        int redY = (int)ownKart.getLocationY();
        int blueX = (int)foreignKart.getLocationX();
        int blueY = (int)foreignKart.getLocationY();

        if ((blueX < redX + 40) && (blueX > redX - 40) && (blueY < redY + 40) && (blueY > redY - 40)) {
            ownKart.setCrashFlag(true);
            foreignKart.setCrashFlag(true);
        }

    }

    public static void resetKarts() {

        foreignKart = null;
        initialise();
        sendMessage("reset_game");
    }

    public static Kart getOwnKart() { return ownKart; }

    public static Kart getForeignKart() { return foreignKart; }

    public static String getKartType() { return kartType; }
}
