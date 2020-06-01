import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RestorePass implements Initializable {

    @FXML
    private TextField txtCorreo;

    @FXML
    private AnchorPane paneCorreoEnviado, paneRestorePass, paneError;

    @FXML
    private ComboBox<String> comboboxUsuario;

    private String correoDummy = "correo_ok@gmail.com";

    ObservableList<String> listaLugares = FXCollections.observableArrayList( "Administrador",
            "Servicios Nutresa", "Protección", "Nacional de Chocolates Bogotá", "Suizo", "Comercial Nutresa",
            "DHL", "Tecnoquímicas San Nicolás", "Smurfit casino principal", "Smurfit Corrugados", "Unilever");


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
        switch (txtCorreo.getText()){
            case "correo_ok@gmail.com":
                this.paneCorreoEnviado.setVisible(true);
                break;
            default:
                this.paneError.setVisible(true);
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.comboboxUsuario.setItems(listaLugares);
    }

    public void closePopupError(javafx.scene.input.MouseEvent event) {
        this.paneError.setVisible(false);
    }
}
