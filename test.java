import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(123)) {
            System.out.println("Server started. Waiting for clients to connect...");

            byte[] originalFileContent = readFile("job.txt");
            System.out.println(originalFileContent.length);
            int segmentSize = originalFileContent.length / 5;
            for (int i = 0; i < 5; i++) {
                System.out.println("Client connected: " + (i + 1) + "/" + 5);

                int startIndex = i * segmentSize + 1;
                int endIndex;
                if (i == 5 - 1) {
                    endIndex = originalFileContent.length;
                } else {
                    endIndex = (i + 1) * segmentSize;
                }
                System.out.println(i + " " + startIndex + " " + endIndex);

                byte[] segment = extractSegment(originalFileContent, startIndex, endIndex);

                System.out.println(segment.length);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile(String fileName, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(content);
        }
    }

    private static byte[] extractSegment(byte[] fileContent, int startIndex, int endIndex) {
        int segmentSize = endIndex - startIndex;
        byte[] segment = new byte[segmentSize];
        System.arraycopy(fileContent, startIndex, segment, 0, segmentSize);
        return segment;
    }

    private static byte[] readFile(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }
}
