import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {
    public static void main(String[] args) {
        int portNumber = 12345;
        int numberOfClients = 5;
        String originalFileName = "job.txt";
        int totalWordCount = 0;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started. Waiting for clients to connect...");

            byte[] originalFileContent = Files.readAllBytes(Paths.get(originalFileName));

            int segmentSize = originalFileContent.length / numberOfClients;

            for (int i = 0; i < numberOfClients; i++) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + (i + 1) + "/" + numberOfClients);

                int startIndex = i * segmentSize;
                int endIndex;
                if (i == numberOfClients - 1) {
                    endIndex = originalFileContent.length;
                } else {
                    endIndex = (i + 1) * segmentSize;
                }
                byte[] segment = extractSegment(originalFileContent, startIndex, endIndex);

                // String segmentContent = new String(segment);
                // // Print the content
                // System.out.println("Segment content: " + segmentContent);

                String segmentFileName = "segment_" + i + ".txt";
                saveToFile(segmentFileName, segment);

                sendFileNameToClient(clientSocket, segmentFileName);

                // BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // String wordCountStr = in.readLine(); 
                // int wordCount = Integer.parseInt(wordCountStr);
                // System.out.println("Client sent word count: " + wordCount);

                // totalWordCount += wordCount;

                // in.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            }
            System.out.println(totalWordCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // this method will extract the specific content for each client. it will return a byte array containing the extracted segment of the file content.
    private static byte[] extractSegment(byte[] fileContent, int startIndex, int endIndex) {
        int segmentSize = endIndex - startIndex;
        byte[] segment = new byte[segmentSize];
        System.arraycopy(fileContent, startIndex, segment, 0, segmentSize);
        return segment;
    }

    // this method will make a file with the content in it.
    private static void saveToFile(String fileName, byte[] content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(content);
        }
    }

    // this method will send the file to client
    private static void sendFileNameToClient(Socket clientSocket, String fileName) throws IOException {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            out.println(fileName);
        }
    }
}
