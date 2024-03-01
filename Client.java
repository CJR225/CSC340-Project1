import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1";
        int serverPort = 12345;
        String fileName = "segment_0.txt";

        try (Socket socket = new Socket(serverIP, serverPort)) {
            System.out.println("Connected to server.");

            InputStream inputStream = socket.getInputStream();
            byte[] content = readFromSocket(inputStream);
            saveToFile(fileName, content);

            int wordCount = WordCount.wordCount("segment_0.txt");
            System.out.println("Number of words in the file: " + wordCount);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(String.valueOf(wordCount));
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
        DataInputStream dis = new DataInputStream(inputStream);
        int size = dis.readInt();
        byte[] data = new byte[size];

        int totalBytesRead = 0;
        while (totalBytesRead < size) {
            int bytesRead = dis.read(data, totalBytesRead, size - totalBytesRead);
            if (bytesRead == -1) {
                break;
            }
            totalBytesRead += bytesRead;
        }
        return data;
    }
}