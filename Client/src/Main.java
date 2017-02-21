import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Runs the client program
 */
public class Main extends Application{

     Scene connectScene;
     Stage stage;

    /**
     * Launches application
     * @param args - Command-line arguments
     * @throws IOException if application returns error
     */
    public static void main(String[] args) throws IOException {
        launch(args);
    }

    /**
     * Opens connect scene
     * @param stage - The stage where the scene is.
     * @throws Exception if FXMLLoader returns an error
     */
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("connect.fxml"));

        connectScene = new Scene(root, 400, 200);

        stage.setTitle("Connect");
        stage.setResizable(false);
        stage.setScene(connectScene);
        stage.show();
    }

}

