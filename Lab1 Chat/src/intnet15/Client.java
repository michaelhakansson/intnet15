package intnet15;
import java.net.Socket;

/**
 * Chat client that connects to a server on port 1337.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        String serverAddress = "127.0.0.1";
        Socket socket = new Socket(serverAddress, 1337);
    }
}
