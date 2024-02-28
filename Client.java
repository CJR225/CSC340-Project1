import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int serverPort = 12345;
        String fileName = "segment_0.txt";

        try (Socket socket = new Socket(serverIP, serverPort)) {
            System.out.println("Connected to server.");

            // try {
            // Thread.sleep(10000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            
            InputStream inputStream = socket.getInputStream();
            byte[] content = readFromSocket(inputStream);

            saveToFile(fileName, content);

            int wordCount = WordCount.wordCount("segment_0.txt");
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

    private static void saveToFile(String fileName, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(content);
        }
    }

    private static byte[] readFromSocket(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
