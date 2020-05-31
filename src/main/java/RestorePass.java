import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class RestorePass {
    @FXML
    private AnchorPane paneCorreoEnviado, paneRestorePass;


    public void closePopupRestorePass(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        this.paneCorreoEnviado.setVisible(false);
        Parent parent = FXMLLoader.load(getClass().getResource("logIn.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.show();
        Stage myStage = (Stage) this.paneRestorePass.getScene().getWindow();
        myStage.close();
    }

    public void btnAceptarRestorePassActionPerformed(ActionEvent actionEvent) {
        this.paneCorreoEnviado.setVisible(true);
    }
}
