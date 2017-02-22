import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by stiangrim on 21.02.2017.


 /**
 * Controls the connect scene.
 */
public class connectController {

    Client client = Client.getInstance();

    @FXML
    Pane pane;
    @FXML
    TextField ip;
    @FXML
    TextField port;

    /**
     * Connects to the server if IP and Port is valid
     *
     * @throws IOException
     */
    @FXML
    void connect() throws IOException {
        Pattern p = Pattern.compile("(\\d+).(\\d+).(\\d+).(\\d+)");
        Matcher m = p.matcher(ip.getText());

        if (!m.find()){
            alertBox("Invalid IP");
            return;
        }

        if(ip.getText().equals("") || port.getText().equals("")) {
            alertBox("Enter IP address and port number");
            return;
        }

        int portNumber = Integer.parseInt(port.getText());
        if(portNumber < 1 || portNumber > 65535) {
            alertBox("Invalid port.\nValid range: 1-65535");
            return;
        }

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip.getText(), portNumber), 2000);
        } catch (UnknownHostException e) {
            alertBox("Invalid IP");
            return;
        } catch (Exception e) {
            alertBox("Could not connect to server.\nCheck your IP and port number.");
            return;
        }

        client.setSocket(socket);
        client.setPrintWriter(new PrintWriter(socket.getOutputStream(), true));
        client.setBufferedReader(new BufferedReader(
                new InputStreamReader(socket.getInputStream())));

        goToLoginScene();
    }

    /**
     * Opens an alert box with a text given by the parameter
     *
     * @param error The text that will appear in the Alert Box
     */
    void alertBox(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Could not connect");
        alert.setContentText(error);
        alert.showAndWait();
    }

    /**
     * Quits the program
     */
    @FXML
    void quit() {
        client.receiver().interrupt();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Switches to the log in scene
     *
     * @throws IOException
     */
    void goToLoginScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));

        Scene loginScene = new Scene(root, 600, 400);
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(loginScene);

        stage.setOnCloseRequest(event -> {
            client.receiver().interrupt();
            Platform.exit();
            System.exit(0);
        });

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();

    }

}
