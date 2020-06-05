import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public Routes routes = Routes.getRoutesClass();

    @FXML
    private AnchorPane dnd;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent logIn = FXMLLoader.load(getClass().getResource("logIn.fxml"));
        primaryStage.setTitle("KitchenWorksDesktop");
        primaryStage.getIcons().add(new Image("\\images\\cubiertos.png"));
        primaryStage.setScene(new Scene(logIn, 600, 400));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
