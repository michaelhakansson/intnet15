import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

public class HttpServer{

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(8000);

        while(true) {
            new Thread(new Connection(ss.accept())).start();
        }
    }

    private static int currentSessionId = 0;

    static HashMap<Integer, Session> sessions = new HashMap();

    private static int addNewSession() {
        int sessionId = ++currentSessionId;
        sessions.put(sessionId, new Session(sessionId));
        return sessionId;
    }

    private static class Session {
        private int sessionId;
        private int lowGuess = 1;
        private int highGuess = 100;
        private int correctNumber;
        private int numberOfGuesses = 0;

        public Session(int sessionId) {
            this.sessionId = sessionId;

            Random rand = new Random();
            // Random number between 1 and 100
            this.correctNumber = rand.nextInt(100) + 1;
        }

        public void setHighGuess(int guess) {
            highGuess = guess;
        }

        public void setLowGuess(int guess) {
            lowGuess = guess;
        }

        public int getHighGuess() {
            return highGuess;
        }

        public int getLowGuess() {
            return lowGuess;
        }

        public int getSessionId() {
            return sessionId;
        }

        public int getCorrectNumber() {
            return correctNumber;
        }

        public int getNumberOfGuesses() {
            return numberOfGuesses;
        }

        public void incrementNumberOfGuesses() {
            ++numberOfGuesses;
        }
    }

    private static class Connection implements Runnable {
        private Socket socket;
        private int sessionId = -1;
        private int guess = -1;

        public Connection(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            BufferedReader request = null;
            try {
                request = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String str = null;

                str = request.readLine(); // Read the filename

                System.out.println(str);
                StringTokenizer tokens = new StringTokenizer(str," ?");

                tokens.nextToken(); // The word "GET"
                String requestedDocument = tokens.nextToken();
                if ((str = tokens.nextToken()).contains("guess=")) {
                    guess = Integer.parseInt( str.split("guess=")[1] );
                }

                // Read the rest of the HTTP request
                while( (str = request.readLine()) != null && str.length() > 0) {
                    if (str.contains("SESSION-ID=")) {
                        sessionId = Integer.parseInt( str.split("SESSION-ID=")[1].split(";")[0] );
                    }
                }

                if (!sessions.containsKey(sessionId)) { // No session exists
                    sessionId = addNewSession(); // Create and add new session
                }

                // Request done
                socket.shutdownInput();

                PrintStream response = null;

                response = new PrintStream(socket.getOutputStream());

                response.println("HTTP/1.0 200 OK");
                response.println("Server : Slask 0.1 Beta");

                if(requestedDocument.contains(".html")) {
                    response.println("Content-Type: text/html");
                }

                if(requestedDocument.contains(".gif")) {
                    response.println("Content-Type: image/gif");
                }

                response.println("Set-Cookie: SESSION-ID=" + currentSessionId + "; expires=Wednesday,31-Dec-15  21:00:00 GMT");
                response.println();
                File f = new File("."+requestedDocument);
                FileInputStream fis = new FileInputStream(f);

                BufferedReader infil = new BufferedReader(new InputStreamReader(fis));

                String responseString;

                while( (responseString=infil.readLine()) != null ){
                    if (responseString.equals("<!-- INSERT DATA HERE -->")) { // Custom data
                        // The current session
                        Session session = sessions.get(sessionId);
                        if (guess != -1) { // A guess has been made
                            session.incrementNumberOfGuesses();
                        }
                        if (session.getNumberOfGuesses() == 0) { // No guess made
                            response.print("Guess a number between 1 and 100.");
                        } else { // Not first guess
                            if (guess == session.getCorrectNumber()) { // Correct guess
                                response.print("You made it! The correct number was " +
                                        session.getCorrectNumber() + ". You made " +
                                        session.getNumberOfGuesses() + " guess(es) \n");
                            } else if (guess < session.getCorrectNumber()) {
                                session.setLowGuess(guess+1);
                                response.print("Too low. Guess a new number between "
                                        + session.getLowGuess() + " and " + session.getHighGuess() + ". \n");
                            } else { // guess > session.getCorrectNumber()
                                session.setHighGuess(guess-1);
                                response.print("Too high. Guess a new number between "
                                        + session.getLowGuess() + " and " + session.getHighGuess() + ". \n");
                            }
                        }
                    } else {
                        response.print(responseString + "\n");
                    }
                }

                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
