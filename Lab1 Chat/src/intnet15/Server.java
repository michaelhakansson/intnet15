package intnet15;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * A multi threaded TCP chat server that runs on port 1337.
 */
public class Server {
    //Contains a list of all the clients
    private static Vector<PrintWriter> writers = new Vector<PrintWriter>();
    private static Queue<String> messages = new LinkedList<String>();

    /**
     * Adds the out channel to a client vector
     */
    public static synchronized void addClient(PrintWriter clientChannel) {
        writers.add(clientChannel);
    }

    /**
     * Removes a client from the client vector
     */
    public static synchronized void removeClient(PrintWriter clientChannel) {
        writers.remove(clientChannel);
    }

    /**
     * Receives messages from a client
     */
    private static synchronized void receiveMessage(String message) {
        messages.add(message);
    }

    /**
     * Sends messages to all the clients
     */
    private static synchronized void distributeMessages() {
        while (!messages.isEmpty()) {
            String message = messages.poll();
            for (PrintWriter writer : writers) {
                writer.println(message);
            }
        }
    }

    /**
     * Runs the chat server
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Chat server has been started");

        ServerSocket listener = new ServerSocket(1337);

        new Thread(new Distributor()).start();

        while (true) {
            new Thread(new Connection(listener.accept())).start();
        }

    }

    /**
     * Class for distributing the messages. This is run in an own thread.
     */
    private static class Distributor implements Runnable {
        public void run() {
            while (true) {
                distributeMessages();
            }
        }
    }
    /**
     * Each connected client receives its own thread for the connection.
     */
    private static class Connection implements Runnable {
        private Socket socket;
        private String username = null;
        private BufferedReader in;
        private PrintWriter out;

        private Connection(Socket socket) {
            this.socket = socket;
            log("New connection at socket " + socket);
        }

        private void log(String message) {
            System.out.println("LOG: " + message);
        }

        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Join chat phase
                while (username == null || username.equals("")) {
                    System.out.println(socket);
                    log("Requesting username for socket " + socket);
                    out.println("ENTER_NAME"); // Ask client for username
                    username = in.readLine(); // Read username from client
                }
                addClient(out); // Add the clients out channel to the client list
                out.println("WELCOME"); // Send welcome status message
                log("User " + username + " has entered the chat");

                // Read messages
                while (true) {
                    String input = in.readLine();
                    if (input != null) {
                        if (input.equals("quit")) {
                            return;
                        }
                        receiveMessage("MESSAGE " + username + ": " + input);
                        log("Message from " + username + " added to message list");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close(); // Close the socket when client is closed
                    removeClient(out);
                    log("User " + username + " left the chat");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
