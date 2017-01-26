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

    String hostName;
    int portNumber;


    Socket socket;

    PrintWriter out;
    BufferedReader in;
    BufferedReader stdIn;


    public Client() throws IOException {

        System.out.println("Lager nytt Client-objekt");

        socket = new Socket("127.0.0.1", 5555);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(
                new InputStreamReader(System.in));
    }

    public Client(String hostName, int portNumber) throws IOException {
        this.hostName = hostName;
        this.portNumber = portNumber;

        socket = new Socket(hostName, portNumber);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        stdIn = new BufferedReader(
                new InputStreamReader(System.in));
    }


    public boolean registrerUser(String username, String password) {
        //Check if username exists before

        return false;
    }

    public void sendMessage(String message) throws IOException {
        String userInput = message;

        System.out.println("Client: " + userInput);
        out.println(userInput);

        String recievedText = in.readLine();

        System.out.println("Server: " + recievedText);
    }

}
