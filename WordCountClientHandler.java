import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WordCountClientHandler extends Thread {
    private final Socket clientSocket;
    private final String filePath;
    private int wordCount;

    public WordCountClientHandler(Socket clientSocket, String filePath) {
        this.clientSocket = clientSocket;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            // Simulate receiving acknowledgment from the client
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            boolean acknowledgment = inputStream.readBoolean();

            if (acknowledgment) {
                // Simulate processing a portion of the text file
                wordCount = countWordsInFile(filePath);
            }

            // Simulate sending the word count back to the server
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.writeInt(wordCount);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getWordCount() {
        return wordCount;
    }

    private int countWordsInFile(String filePath) {
        try {
            return WordCount.wordCount(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
