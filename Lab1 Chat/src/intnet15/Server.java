package intnet15;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A multi threaded TCP chat server that runs on port 1337.
 */
public class Server {

    /**
     * Runs the chat server
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Chat server has been started");

        ServerSocket listener = new ServerSocket(1337);

        try {
            while (true) {
                new CommunicationChannel(listener.accept()).start();
            }
        } finally {
            listener.close();
            System.out.println("Chat server is closed");
        }
    }

    private static class CommunicationChannel extends Thread {
        private Socket socket;

        public CommunicationChannel(Socket socket) {
            this.socket = socket;
            System.out.println("New connection at socket " + socket);
        }
    }
}
