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
            int lastIndexUsed = 0;

            for (int i = 0; i < numberOfClients; i++) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + (i + 1) + "/" + numberOfClients);

                    int startIndex = lastIndexUsed;
                    int endIndex = originalFileContent.length;

                    if (i < numberOfClients - 1) {
                        endIndex = (i + 1) * segmentSize;
                        while (endIndex > startIndex && originalFileContent[endIndex - 1] != ' '
                                && originalFileContent[endIndex - 1] != '\n') {
                            endIndex--;
                        }
                    }

                    lastIndexUsed = endIndex;

                    byte[] segment = extractSegment(originalFileContent, startIndex, endIndex);
                    sendFileToClient(clientSocket, segment);

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String wordCountStr = in.readLine();
                    if (wordCountStr != null) {
                        int wordCount = Integer.parseInt(wordCountStr);
                        System.out.println("Client sent word count: " + wordCount);
                        totalWordCount += wordCount;
                    } else {
                        System.out.println("No word count received, client may have closed the connection.");
                    }

                    in.close();
                    clientSocket.close();
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    System.out.println("An error occurred with a client connection.");
                    e.printStackTrace();
                }
            }
            System.out.println("Total word count from all clients: " + totalWordCount);
        } catch (IOException e) {
            System.out.println("An error occurred starting the server.");
            e.printStackTrace();
        }
    }

    private static byte[] extractSegment(byte[] fileContent, int startIndex, int endIndex) {
        int segmentSize = endIndex - startIndex;
        byte[] segment = new byte[segmentSize];
        System.arraycopy(fileContent, startIndex, segment, 0, segmentSize);
        return segment;
    }

    private static void sendFileToClient(Socket clientSocket, byte[] fileContent) throws IOException {
    	try {
            BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
            bos.write(fileContent, 0, fileContent.length);
            bos.flush();
        } catch (IOException e) {
            System.err.println("Error occurred while sending file to client: " + e.getMessage());
        }
    }
}