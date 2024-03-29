package intnet15;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Chat client that connects to a server on port 1337.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Enter server address: ");
        String serverAddress = new BufferedReader(new InputStreamReader(System.in)).readLine();

        Socket socket = new Socket(serverAddress, 1337);
        BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Send message thread
        new Thread(new SendMessage(out)).start();

        // Read messages from the server
        String data;
        while ((data = in.readLine()) != null) {
            if (data.equals("ENTER_NAME")) { // Server asks for name
                System.out.println("Enter your name: ");
                // SendMessage thread handles the sending of the name
            } else if (data.startsWith("MESSAGE")) { // Message on the form "MESSAGE <username>: <message>"
                System.out.println(data.substring(8)); // Removes the "MESSAGE " part of the data
            } else if (data.equals("WELCOME")) {
                System.out.println("You have now joined the chat");
            }
        }
    }
}

/**
 * Read input and send input as message to the server.
 */
class SendMessage implements Runnable {
    private PrintWriter out;
    String message;

    public SendMessage(PrintWriter out) {
        this.out = out;
    }

    public void run() {
        try {
            while (true) {
                message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.println(message);
                if (message.equals("quit")) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
