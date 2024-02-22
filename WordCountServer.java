import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordCountServer {
    private static final int PORT = 12345;
    private static final int NUM_CLIENTS = 5;
    private static final String FILE_PATH = "job.txt";

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            ExecutorService executorService = Executors.newFixedThreadPool(NUM_CLIENTS);
            List<WordCountClientHandler> clients = new ArrayList<>();

            for (int i = 0; i < NUM_CLIENTS; i++) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from client: " + (i + 1));

                WordCountClientHandler clientHandler = new WordCountClientHandler(clientSocket, FILE_PATH);
                clients.add(clientHandler);
                executorService.submit(clientHandler);
            }

            // Wait for all clients to finish
            for (WordCountClientHandler client : clients) {
                client.join();
            }

            // Aggregate word counts from all clients
            int totalWordCount = 0;
            for (WordCountClientHandler client : clients) {
                totalWordCount += client.getWordCount();
            }

            System.out.println("Total Word Count: " + totalWordCount);

            executorService.shutdown();
            serverSocket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}