import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Client {

    private static Client instance = null;
    private String hostName = "127.0.0.1";
    private String username = "";
    String received;
    private int portNumber = 5555;
    private boolean connected;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private BufferedReader stdIn = null;
    private TextArea chatArea;
    private Button connectButton;
    public boolean firstConnectionDone;
    public String lastConnectedUser = "";

    private TableView<User> tableView;
    private TableColumn<User, Circle> statusColumn;
    private TableColumn<User, String> nameColumn;

    ObservableList<User> users = FXCollections.observableArrayList();

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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setChatArea(TextArea chatArea) {
        this.chatArea = chatArea;
    }

    public void setConnectButton(Button connectButton) {
        this.connectButton = connectButton;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
        this.tableView.setPlaceholder(new Label(""));
    }

    public void setStatusColumn(TableColumn statusColumn) {
        this.statusColumn = statusColumn;
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("circle"));
    }

    public void setNameColumn(TableColumn nameColumn) {
        this.nameColumn = nameColumn;
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    public String getLastConnectedUser() {
        return lastConnectedUser;
    }

    public void setLastConnectedUser(String lastConnectedUser) {
        this.lastConnectedUser = lastConnectedUser;
    }

    public void updateOnlineUsers(String userString) {

        Platform.runLater(() -> {
            char status = ' ';

            // Deletes all items in ListView
            tableView.getItems().clear(); //????

            char[] charArray = userString.toCharArray();

            StringBuilder sb = new StringBuilder();
            for (int i = 5; i < charArray.length; i++) {

                //User is available
                if (charArray[i - 1] == 'a' && charArray[i] == ':') {
                    //Do something
                    status = 'a';
                    continue;
                    //User is busy
                } else if (charArray[i - 1] == 'b' && charArray[i] == ':') {
                    //Do something
                    status = 'b';
                    continue;
                    //User is offline
                } else if (charArray[i - 1] == 'o' && charArray[i] == ':') {
                    //Do something
                    status = 'o';
                    continue;
                } else if (charArray[i - 1] == ' ') {
                    continue;
                }

                if (charArray[i] != ' ') {
                    sb.append(charArray[i]);
                } else {
                    if (!sb.toString().equals(username)) {
                        //Adds all items to ListView, except the user himself
                        switch (status) {
                            case 'a':
                                users.add(new User(new Circle(8, Color.GREEN), sb.toString()));
                                break;
                            case 'b':
                                users.add(new User(new Circle(8, Color.ORANGE), sb.toString()));
                                break;
                            case 'o':
                                users.add(new User(new Circle(8, Color.GRAY), sb.toString()));
                                break;
                        }
                    }
                    sb.setLength(0);
                }
            }

            tableView.setItems(users);
        });
    }

    public Thread receiver() {
        return new Thread(() -> {
            try {
                while ((received = in.readLine()) != null) {
                    System.out.println(received);

                    //Message from server
                    if (received.charAt(0) == '*') {
                        if (received.substring(0, 3).equals("*c*")) {
                            //Changes button appearance
                            if (!firstConnectionDone) {
                                Platform.runLater(() -> {
                                    connectButton.setText("Reconnect");
                                });
                                firstConnectionDone = true;
                            }
                            lastConnectedUser = received.substring(3, received.length());
                            connected = true;
                            chatArea.setText("<Connected to " + received.substring(3, received.length()) + ">\n");
                        } else if (received.substring(0, 3).equals("*d*")) {
                            out.println("ok");
                            connected = false;
                            chatArea.setText("<Disconnected from " + received.substring(3, received.length()) + ">\n");
                        } else if (received.substring(0, 4).equals("*ui*")) {
                            updateOnlineUsers(received);
                        } else if (received.substring(0, 4).equals("*c?*")) {
                            connectRequest(received);
                        }
                    } else {
                        chatArea.appendText(received + '\n');
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Opens up a pop-up box. Asks if you want to connect
    // to an incoming user or not
    public void connectRequest(String receivedText) {
        Platform.runLater(() -> {

            String user = receivedText.substring(4, receivedText.length());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connect?");
            alert.setHeaderText(user + " wants to chat with you!");
            alert.setContentText("Do you accept?");
            alert.initOwner(chatArea.getScene().getWindow());

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");

            alert.getButtonTypes().setAll(yes, no);

            Optional<ButtonType> result = alert.showAndWait();

            System.out.println("result.isPresent() = " + result.isPresent());
            if (result.isPresent() && result.get().equals(yes)) {
                System.out.println("RESULT IS PRESENT, user: <" + user + '>');
                out.println("*QUIT*");
                out.println("*OK*" + user);
            }else{
                System.out.println("Sending *no* to the server");
                out.println("*no*" + user);
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
        if (connected) {
            out.println(message);
        }
    }

    protected void finalize() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
