import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int portNumber = 12345;
    private static final int numberOfClients = 5;
    private static final String originalFileName = "job.txt";
    private static int totalWordCount = 0;
    private static int clientCount = 0;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server started. Waiting for clients to connect...");

            byte[] originalFileContent = Files.readAllBytes(Paths.get(originalFileName));
            int segmentSize = originalFileContent.length / numberOfClients;
            List<byte[]> segments = new ArrayList<>();

            // compute segments
            int lastIndexUsed = 0;
            for (int i = 0; i < numberOfClients; i++) {
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
                segments.add(extractSegment(originalFileContent, startIndex, endIndex));
            }

            // Accept clients
            List<Socket> clientSockets = new ArrayList<>();
            while (clientCount < numberOfClients) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("Client connected: " + (++clientCount) + "/" + numberOfClients);
            }

            // Once all clients are connected, distribute segments and process responses
            for (int i = 0; i < numberOfClients; i++) {
                final Socket clientSocket = clientSockets.get(i);
                final byte[] segment = segments.get(i);

                new Thread(() -> {
                    try {
                        sendFileToClient(clientSocket, segment);

                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String wordCountStr = in.readLine();
                        if (wordCountStr != null) {
                            synchronized (Server.class) {
                                totalWordCount += Integer.parseInt(wordCountStr);
                            }
                            System.out.println("Client sent word count: " + wordCountStr);
                        }

                        in.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        System.out.println("An error occurred with a client connection.");
                        e.printStackTrace();
                    }
                }).start();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Total word count from all clients: " + totalWordCount);
        } catch (IOException e) {
            System.out.println("An error occurred starting the server.");
            e.printStackTrace();
        }
    }

    private static byte[] extractSegment(byte[] fileContent, int startIndex, int endIndex) {
        byte[] segment = new byte[endIndex - startIndex];
        System.arraycopy(fileContent, startIndex, segment, 0, segment.length);
        return segment;
    }

    private static void sendFileToClient(Socket clientSocket, byte[] fileContent) throws IOException {
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        dos.writeInt(fileContent.length);
        dos.write(fileContent, 0, fileContent.length);
        dos.flush();
    }
}
