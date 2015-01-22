package intnet15;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Chat client that connects to a server on port 1337.
 */
public class Client {

    private String askForServerAddress() throws Exception {
        System.out.println("Enter server address: ");
        String serverAddress = new BufferedReader(new InputStreamReader(System.in)).readLine();
        return serverAddress;
    }

    public void run () throws Exception {
        Socket socket = new Socket(askForServerAddress(), 1337);

        BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


        while (true) {
            String data = in.readLine();

            System.out.println(data);

            // Server asks for name
            if (data.equals("ENTER_NAME")) {
                System.out.println("Enter your name: ");
                String name = new BufferedReader(new InputStreamReader(System.in)).readLine();
                out.println(name); // Send the name to the server
            } else if (data.equals("MESSAGE")) {

            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }

}
