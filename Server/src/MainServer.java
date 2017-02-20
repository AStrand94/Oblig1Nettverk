import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sun.applet.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dusja on 06.02.2017.
 */
public class MainServer extends Application{
    static Scene serverScene;
    static Stage stage;



    public static void main(String[] args) {
        Application.launch(MainServer.class,(String[])null);
    }

    public void start(Stage stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("./server.fxml"));
            this.stage = stage;
            serverScene = new Scene(root, 700, 400);
            stage.setTitle("Server");
            stage.setScene(serverScene);
            stage.show();

            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });

        }catch (Exception e) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE,null,e);
        }
    }

    protected void finalize(){
        Platform.exit();
        System.exit(0);
    }
}
