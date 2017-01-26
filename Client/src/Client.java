import javafx.fxml.FXML;

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

    String hostName = "127.0.0.1";
    int portNumber = 5555;


    Socket socket = null;

    PrintWriter out = null;
    BufferedReader in = null;
    BufferedReader stdIn = null;


    protected Client() throws IOException {

        socket = new Socket(hostName, portNumber);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(
                new InputStreamReader(System.in));
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

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public boolean updateSocket() {
        try {
            socket = new Socket(hostName, portNumber);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String sendMessage(String message) throws IOException {
        String userInput = message;

        System.out.println("Client: " + userInput);
        out.println(userInput);

        String recievedText = in.readLine();

        System.out.println("Server: " + recievedText);

        return recievedText;
    }

}
