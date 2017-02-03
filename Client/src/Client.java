import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Client {

    private static Client instance = null;

    private String hostName = "127.0.0.1";
    private int portNumber = 5555;


    private Socket socket = null;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private BufferedReader stdIn = null;

    private TextArea chatArea;

    public BufferedReader read() {
        return in;
    }

    public PrintWriter print() {
        return out;
    }

    protected Client() throws IOException {

        socket = new Socket(hostName, portNumber);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(
                new InputStreamReader(System.in));
    }

    public void setChatArea(TextArea chatArea){
        this.chatArea = chatArea;
    }

    public static Client getInstance() {
        if (instance == null) {
            try {
                instance = new Client();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Thread receiver() {
        return new Thread(() -> {
            try {
                String received;
                while ((received = in.readLine()) != null) {
                    System.out.println(received);
                    chatArea.appendText(received + '\n');

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean createNewSocket(String hostName, int portNumber) {
        try {
            socket = new Socket(hostName, portNumber);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String sendMessage(String message) throws IOException {
        System.out.println(message);
        out.println(message);

        return message;
    }

}
