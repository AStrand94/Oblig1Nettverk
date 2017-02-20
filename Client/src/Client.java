import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.LoadException;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import jdk.nashorn.internal.runtime.ECMAException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
     * @return Client
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
        try {
            connectToServer();
        } catch (Exception e) {
            System.out.println("FIKS");
        }

        out = new PrintWriter(socket.getOutputStream(), true);
        in = null;
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        username = "";
        lastConnectedUser = "";
    }

    void connectToServer() {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connect");
        dialog.setHeaderText("Please connect to server");

        ButtonType connect = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = ButtonType.CANCEL;
        dialog.getDialogPane().getButtonTypes().addAll(connect, cancel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ipAddress = new TextField();
        ipAddress.setText("127.0.0.1");
        ipAddress.setPromptText("IP address");
        TextField portNumber = new TextField();
        portNumber.setText("5555");
        portNumber.setPromptText("Port number");

        grid.add(new Label("IP address:"), 0, 0);
        grid.add(ipAddress, 1, 0);
        grid.add(new Label("Port number:"), 0, 1);
        grid.add(portNumber, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connect) {
                return new Pair<>(ipAddress.getText(), portNumber.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                socket = new Socket(pair.getKey(), Integer.parseInt(pair.getValue()));
            } catch (UnknownHostException host) {
                System.out.println("Unknown host");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException runtime) {
                System.out.println("Make sure you have a server running.");
            }
        });

    }

    /**
     * Sets the username of the logged in user.
     *
     * @param username The username of the logged in person.
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the logged in user.
     *
     * @return String
     */
    String getUsername() {
        return username;
    }

    /**
     * Returns true if the user is connected to someone,
     * and false if not connected
     *
     * @return boolean
     */
    boolean getConnected() {
        return connected;
    }

    /**
     * Sets and gives the Client object a reference to the chat area.
     *
     * @param chatArea the chat area where all messages appear
     */
    void setChatArea(TextArea chatArea) {
        this.chatArea = chatArea;
    }

    /**
     * Sets and gives the Client object a reference to the connect button.
     *
     * @param connectButton the connect button
     */
    void setConnectButton(Button connectButton) {
        this.connectButton = connectButton;
    }

    /**
     * Sets and gives the Client object a reference to the Table View.
     * Sets the prompt text of the users view to an empty text.
     *
     * @param tableView the user list
     */
    void setTableView(TableView tableView) {
        this.tableView = tableView;
        this.tableView.setPlaceholder(new Label(""));
    }

    /**
     * Sets and gives the Client object a reference to the status column.
     *
     * @param statusColumn the status list
     */
    void setStatusColumn(TableColumn statusColumn) {
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("circle"));
    }

    /**
     * Sets and gives the Client object a reference to the name column.
     *
     * @param nameColumn the name list
     */
    void setNameColumn(TableColumn nameColumn) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    /**
     * Returns the username of the last person the user is connected with.
     *
     * @return String
     */
    public String getLastConnectedUser() {
        return lastConnectedUser;
    }

    /**
     * Updates the users list along with their status.
     *
     * @param userString the full user list received from the Socket as a String
     */
    void updateOnlineUsers(String userString) {

        Platform.runLater(() -> {
            char status = ' ';

            // Deletes all items in ListView
            tableView.getItems().clear();

            char[] charArray = userString.toCharArray();

            StringBuilder sb = new StringBuilder();
            for (int i = 5; i < charArray.length; i++) {

                //User is available
                if (charArray[i - 1] == 'a' && charArray[i] == ':') {
                    status = 'a';
                    continue;
                    //User is busy
                } else if (charArray[i - 1] == 'b' && charArray[i] == ':') {
                    status = 'b';
                    continue;
                    //User is offline
                } else if (charArray[i - 1] == 'o' && charArray[i] == ':') {
                    status = 'o';
                    continue;
                } else if (charArray[i - 1] == ' ') {
                    continue;
                }

                if (charArray[i] != ' ') {
                    sb.append(charArray[i]);
                } else {
                    if (!sb.toString().equals(username)) {
                        //Adds all items to the ObservableList, except the user himself
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

            //Adds all users from the ObservableList.
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
     * @param receivedText the String received from the Socket
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
            } else {
                System.out.println("Sending *no* to the server");
                out.println("*no*" + user);
            }
        });
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
