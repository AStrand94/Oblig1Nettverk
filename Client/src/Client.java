import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.xml.soap.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Client {

    private static Client instance = null;

    private String hostName = "127.0.0.1";
    private int portNumber = 5555;

    private boolean connected;

    private Socket socket = null;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private BufferedReader stdIn = null;

    private TextArea chatArea;
    private ListView<String> onlineUsers;

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

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setChatArea(TextArea chatArea){
        this.chatArea = chatArea;
    }

    public void setOnlineUsers(ListView<String> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    //TODO fikse farge etter status på brukerne
    public void updateOnlineUsers(String users) {

        char[] charArray = users.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 5; i < charArray.length; i++) {

            //User is available
            if(charArray[i-1] == 'a' && charArray[i] == ':') {
                //Do something
                continue;
                //User is busy
            } else if(charArray[i-1] == 'b' && charArray[i] == ':') {
                //Do something
                continue;
                //User is offline
            } else if(charArray[i-1] == 'o' && charArray[i] == ':') {
                //Do something
                continue;
            }



            if(charArray[i] != ' ')  {
                sb.append(charArray[i]);
            }
            else {
                onlineUsers.getItems().add(sb.toString());
                sb.setLength(0);
            }

        }
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

                    //Message from server
                    if(received.charAt(0) == '*') {
                        //Update usersOnline
                        if(received.substring(0, 3).equals("*ui*")) {
                            updateOnlineUsers(received);
                        } else if (received.substring(0, 2).equals("*d*")) {
                            connected = false;
                        } else if (received.substring(0, 2).equals("*c*")) {
                            connected = true;
                        }
                    }

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

    public void sendMessageToServer(String message) throws IOException {
        System.out.println(message);
        if(connected) {
            out.println(message);
        }
    }

}
