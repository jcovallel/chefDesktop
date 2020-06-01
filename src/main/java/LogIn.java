import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class LogIn implements Initializable{

    @FXML
    private TextField txtUsuario;

    @FXML
    private AnchorPane parentPane, paneError;

    @FXML
    private PasswordField txtPass;

    @FXML
    private ComboBox<String> comboboxUsuario;

    @FXML
    private Button btnAceptar;

    private static final String pathUsuarios = "/chef/getusers/";
    Helper helper = new Helper();

    private static final String defaultPass = "0000";

    ObservableList<String> listaLugares = FXCollections.observableArrayList( "Administrador",
            "Servicios Nutresa", "Protección", "Nacional de Chocolates Bogotá", "Suizo", "Comercial Nutresa",
            "DHL", "Tecnoquímicas San Nicolás", "Smurfit casino principal", "Smurfit Corrugados", "Unilever");

    @FXML
    public void contrasenaOlvidadaActionPerformed(MouseEvent event) throws IOException {
        helper.show("restorePass.fxml", parentPane);
    }


    @FXML
    public void btnAceptarActionPerformed(ActionEvent event) throws IOException {

        String defaultPassHashed = helper.hash(defaultPass);
        String passHashed = helper.hash(txtPass.getText());
        String passStored = "";

        if(passStored.equals(passHashed) && passStored.equals(defaultPassHashed)){
            helper.show("cambioPass.fxml", parentPane);
        }

        String privilegio = txtPass.getText();

        switch(privilegio){
            case "uno":
                if(!this.comboboxUsuario.getValue().equals("Administrador")){
                    helper.hash(this.txtPass.getText());
                    helper.show("usuarioNormal.fxml", parentPane);
                    break;
                }
                paneError.setVisible(true);
                break;
            case "dos":
                if(this.comboboxUsuario.getValue().equals("Administrador")){
                    helper.show("usuarioAdmin.fxml", parentPane);
                    break;
                }
                paneError.setVisible(true);
                break;
            default:
                paneError.setVisible(true);
                break;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JSONArray jsonArray = null;
        try{
            jsonArray = helper.GET(pathUsuarios);
        }
        catch(IOException ioe){

        }
        if(jsonArray != null){
            for(Object jsonIterator : jsonArray){
                System.out.println(jsonIterator);
            }
        }
        this.comboboxUsuario.setItems(listaLugares);
    }

    public void closePopupError(MouseEvent event) {
        paneError.setVisible(false);
    }

}
