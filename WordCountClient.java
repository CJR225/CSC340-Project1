import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WordCountClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server");

            // Simulate sending acknowledgment to the server
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeBoolean(true);
            outputStream.flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


