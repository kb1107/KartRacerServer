import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private Socket server = null;
    // Declare input stream from client
    private BufferedReader inputStream;
    private String line; // holds message from client
    // Declare an output stream to client
    private DataOutputStream outputStream;

    private static Kart kartRed = null;
    private static Kart kartBlue = null;

    private String kartType; // Either "red" or "blue"

    private boolean alive = true;

    private static boolean playerActive = true; // Used to notify client when opponent exits

    public ClientHandler(Socket server) {
        this.server = server;
    }

    public void run() {
        try {
            inputStream = new BufferedReader(
                    new InputStreamReader(
                            server.getInputStream()
                    )
            );

            outputStream = new DataOutputStream(
                    server.getOutputStream()
            );

            do {
                line = receiveMessage();

                if (line != null) {
                    handleClientResponse(line);
                }

                if (line.equals("CLOSE")) {
                    break;
                }

                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            } while (true);

            // close down server
            outputStream.close();
            inputStream.close();
            server.close();
        }
        catch (Exception e) {
            System.out.println("ClientHandler Exception: " + e.getMessage());
        }

        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    private void sendMessage(String message) {
        try {
            outputStream.writeBytes(message + "\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String receiveMessage() {
        try {
            return inputStream.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void sendForeignKart() {

        switch (kartType) {
            case "blue":

                //direction, locationX, locationY, lapsLeft, crashFlag
                sendMessage("foreign_kart_update " +
                        kartRed.getDirection() + " " +
                        kartRed.getLocationX() + " " +
                        kartRed.getLocationY() + " " +
                        kartRed.getLapsLeft() + " " +
                        kartRed.getCrashFlag()
                );

                break;

            case "red":

                //direction, locationX, locationY, lapsLeft, crashFlag
                sendMessage("foreign_kart_update " +
                        kartBlue.getDirection() + " " +
                        kartBlue.getLocationX() + " " +
                        kartBlue.getLocationY() + " " +
                        kartBlue.getLapsLeft() + " " +
                        kartBlue.getCrashFlag()
                );

                break;
        }
    }


    private void handleClientResponse(String response) {
        System.out.println("CLIENT " + kartType + " SAID: " + response);

        // "identify red" => [ "identify", "red" ]
        // "kart_update" => [ "kart_update" ]
        String[] responseParts = response.split(" ");

        switch (responseParts[0]) {
            case "ping" -> {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
                sendMessage("pong");
            }
            case "identify" -> {
                kartType = responseParts[1];

                switch (kartType) {
                    case "blue":
                        kartBlue = new Kart(kartType);
                        break;

                    case "red":
                        kartRed = new Kart(kartType);
                        break;
                }
            }
            case "own_kart_update" -> {

                switch (kartType) {
                    case "blue":

                        // needed when second player starts new game
                        if (kartBlue == null && playerActive) {
                            kartBlue = new Kart(kartType);
                        }

                        // direction, locationX, locationY, lapsLeft, crashFlag
                        kartBlue.updateDirection(Integer.parseInt(responseParts[1]));
                        kartBlue.setLocationX(Double.parseDouble(responseParts[2]));
                        kartBlue.setLocationY(Double.parseDouble(responseParts[3]));
                        kartBlue.setLapsLeft(Integer.parseInt(responseParts[4]));
                        kartBlue.setCrashFlag(Boolean.valueOf(responseParts[5]));

                        if (kartRed != null) {
                            sendForeignKart();
                        }

                        if (!playerActive) {
                            kartRed = null;
                            sendMessage("player_exit");
                        }

                        break;

                    case "red":

                        // needed when second player starts new game
                        if (kartRed == null && playerActive) {
                            kartRed = new Kart(kartType);
                        }

                        // direction, locationX, locationY, lapsLeft, crashFlag
                        kartRed.updateDirection(Integer.parseInt(responseParts[1]));
                        kartRed.setLocationX(Double.parseDouble(responseParts[2]));
                        kartRed.setLocationY(Double.parseDouble(responseParts[3]));
                        kartRed.setLapsLeft(Integer.parseInt(responseParts[4]));
                        kartRed.setCrashFlag((Boolean.valueOf(responseParts[5])));

                        if (kartBlue != null) {
                            sendForeignKart();
                        }

                        if (!playerActive) {
                            kartBlue = null;
                            sendMessage("player_exit");
                        }

                        break;
                }
            }

            case "reset_game" -> {

                switch (kartType) {
                    case "blue":

                        kartRed = null;

                        break;

                    case "red":

                        kartBlue = null;

                        break;

                }

            }

            case "player_exit" -> {
                playerActive = false;
            }
        }
    }
}
