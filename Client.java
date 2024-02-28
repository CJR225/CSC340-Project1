import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int serverPort = 12345;

        try (Socket socket = new Socket(serverIP, serverPort)) {
            System.out.println("Connected to server.");

            InputStream in = socket.getInputStream();
            File targetFile = new File("received_file.txt");
            try (FileOutputStream fileOut = new FileOutputStream(targetFile)) {
                byte[] bytes = new byte[4096];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    fileOut.write(bytes, 0, count);
                }
            }
            int wordCount = WordCount.wordCount("received_file.txt");
            System.out.println("Number of words in the file: " + wordCount);

            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                writer.println(wordCount);
                writer.flush();
            } // Auto-close writer here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
