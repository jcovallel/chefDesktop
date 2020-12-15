import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public Routes routes = Routes.getRoutesClass();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent logIn = FXMLLoader.load(getClass().getResource("logIn.fxml"));
        primaryStage.setTitle("KitchenWorksDesktop");
        //primaryStage.getIcons().add(new Image("\\images\\cubiertos.png"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/cubiertos.png")));
        //primaryStage.setScene(new Scene(logIn, 600, 400));
        primaryStage.setScene(new Scene(logIn, 650, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
