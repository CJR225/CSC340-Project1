import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int serverPort = 12345;

        try (Socket socket = new Socket(serverIP, serverPort)) {
            System.out.println("Connected to server.");

            // try {
            // Thread.sleep(10000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String filePath = reader.readLine();

            int wordCount = WordCount.wordCount(filePath);
            System.out.println("Number of words in the file: " + wordCount);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(String.valueOf(wordCount));
            writer.flush();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
