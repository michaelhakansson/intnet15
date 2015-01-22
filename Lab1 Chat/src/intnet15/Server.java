package intnet15;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                new Connection(listener.accept()).start();
            }
        } finally {
            listener.close();
            System.out.println("Chat server is closed");
        }
    }

    /**
     * A communication channel on its own thread
     * */
    private static class Connection extends Thread {
        private Socket socket;
        private String name = null;
        private BufferedReader in;
        private PrintWriter out;

        public Connection(Socket socket) {
            this.socket = socket;
            System.out.println("New connection at socket " + socket);
        }

        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (name == null) {
                    System.out.println("LOG: Requesting name");
                    out.println("ENTER_NAME");
                    name = in.readLine();
                    System.out.println("LOG: User " + name + " has entered the chat");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
