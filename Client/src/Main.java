import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by stiangrim on 25.01.2017.
 */
public class Main extends Application{

     Scene loginScene;
     Stage stage;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("connect.fxml"));

        loginScene = new Scene(root, 600, 400);

        stage.setTitle("Connect");
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

}
