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

/**
 * The Client class represent the user.
 */
public class Client {

    private static Client instance = null;
    private String username;
    private String lastConnectedUser;
    private String received;
    private boolean connected;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in;
    private TextArea chatArea;
    private Button connectButton;
    private boolean firstConnectionDone;

    private TableView<User> tableView;

    private ObservableList<User> users = FXCollections.observableArrayList();

    /**
     * Returns the same object of Client to all requests (Singleton).
     *
     * @return Client an instance of the Client object
     */
    static Client getInstance() {
        if (instance == null) {
            try {
                instance = new Client();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Returns the BufferedReader to read from socket.
     *
     * @return BufferedReader
     */
    BufferedReader read() {
        return in;
    }

    /**
     * Returns the PrintWriter to write to the socket.
     *
     * @return PrintWriter
     */
    PrintWriter print() {
        return out;
    }

    /**
     * Instantiates the Socket, PrintWriter and BufferedReader.
     *
     * @throws IOException
     */
    private Client() throws IOException {
        int portNumber = 5555;
        String hostName = "127.0.0.1";
        socket = new Socket(hostName, portNumber);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = null;
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        username = "";
        lastConnectedUser = "";
    }

    /**
     * Sets the username of the logged in user.
     *
     * @param username
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the logged in user.
     *
     * @return String username
     */
    String getUsername() {
        return username;
    }

    /**
     * Returns true if the user is connected to someone,
     * and false if not connected
     *
     * @return boolean connected
     */
    boolean getConnected() {
        return connected;
    }

    /**
     * Sets and gives the Client object a reference to the chat area.
     *
     * @param chatArea
     */
    void setChatArea(TextArea chatArea) {
        this.chatArea = chatArea;
    }

    /**
     * Sets and gives the Client object a reference to the connect button.
     *
     * @param connectButton
     */
    void setConnectButton(Button connectButton) {
        this.connectButton = connectButton;
    }

    /**
     * Sets and gives the Client object a reference to the Table View.
     * Sets the prompt text of the users view to an empty text.
     *
     * @param tableView
     */
    void setTableView(TableView tableView) {
        this.tableView = tableView;
        this.tableView.setPlaceholder(new Label(""));
    }

    /**
     * Sets and gives the Client object a reference to the status column.
     *
     * @param statusColumn
     */
    void setStatusColumn(TableColumn statusColumn) {
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("circle"));
    }

    /**
     * Sets and gives the Client object a reference to the name column.
     *
     * @param nameColumn
     */
    void setNameColumn(TableColumn nameColumn) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    /**
     * Returns the username of the last person the user is connected with.
     *
     * @return String lastConnectedUser
     */
    public String getLastConnectedUser() {
        return lastConnectedUser;
    }

    /**
     * Sets the String of the last person the user is connected with.
     *
     * @param lastConnectedUser
     */
    public void setLastConnectedUser(String lastConnectedUser) {
        this.lastConnectedUser = lastConnectedUser;
    }

    /**
     * Updates the users list along with their status.
     *
     * @param userString
     */
    void updateOnlineUsers(String userString) {

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

    /**
     * Returns a Thread that constantly waits and receives information from the Socket.
     *
     * @return Thread
     */
    Thread receiver() {
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
                            lastConnectedUser = "";
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

    /**
     * Opens a popup box that asks if you want to connect to a user that
     * has requested a chat with you.
     *
     * @param receivedText
     */
    private void connectRequest(String receivedText) {
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

    /**
     * Sends a message from the message field to the Socket,
     * if you are connected with someone
     *
     * @param message
     * @throws IOException
     */
    void sendMessageToServer(String message) throws IOException {
        if (connected) {
            out.println(message);
        }
    }

    /**
     * Closes the socket when the user exits the chat window.
     */
    protected void finalize() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
