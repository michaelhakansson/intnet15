import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * A client that connects to the HTTP server and plays
 * the game 100 times, and then prints the average number
 * of guesses needed.
 */
public class HttpClientBonus {
    private static Vector<Integer> guesses = new Vector<Integer>();

    public static void main(String[] args) {

        // Play the game 100 times
        for (int i = 0; i < 100; ++i) {
            String sessionCookie = null;
            int numberOfGuesses = 0;
            int lowGuess = 0;
            int highGuess = 100;
            int currentGuess;
            Boolean activeGame = true;

            // Will continue until the game is "won".
            while (activeGame) {
                // Set URL
                URL url = null;

                // Make a guess
                currentGuess = ((lowGuess+highGuess)/2);
                try {
                    url = new URL("http","localhost",8000,"/guess.html" + "?guess=" + currentGuess);
                    ++numberOfGuesses;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // Prepare for connection
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection)url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Set header properties
                con.setRequestProperty("User-Agent", "FRA Browser");
                if (sessionCookie != null) {
                    con.setRequestProperty("Cookie", sessionCookie);
                }

                // Connect to the server
                try {
                    con.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Retrieve sessionId cookie if not already done
                if (sessionCookie == null) {
                    sessionCookie = con.getHeaderField("Set-Cookie");
                }

                // Read answer from the server
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Read each row in the answer
                String row = null;
                try {
                    while ((row = in.readLine()) != null) {
                        // Print message body of answer
                        System.out.println(row);

                        // Check result of the guess
                        if (row.contains("Too low")) {
                            lowGuess = currentGuess+1;
                        } else if (row.contains("Too high")) {
                            highGuess = currentGuess-1;
                        } else if (row.contains("You made it")) {
                            activeGame = false;
                            guesses.add(numberOfGuesses);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Number of guesses this round: " + numberOfGuesses);
        }

        // Print game statistics
        int total = 0;
        for (int num : guesses) {
            total += num;
        }
        System.out.println("\nStatistics:");
        System.out.println("Number of rounds played: " + guesses.size());
        System.out.println("Total number of guesses: " + total);
        System.out.println("Average number of guesses: " + (float)total/guesses.size());

    }

}
