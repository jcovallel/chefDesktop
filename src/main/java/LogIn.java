import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.security.*;


public class LogIn {

    @FXML
    private TextField txtUsuario;

    @FXML
    private AnchorPane panelUserPassIncorrecto;

    @FXML
    private PasswordField txtPass;

    @FXML
    private Button btnAceptar;

    private static final String algoritmoHash = "SHA-256";

    @FXML
    public void contrasenaOlvidadaActionPerformed(MouseEvent event) throws IOException {
        show("restorePass.fxml");
    }

    @FXML
    public void btnAceptarActionPerformed(ActionEvent event) throws IOException {

        String privilegio = txtPass.getText();

        switch(privilegio){
            case "uno":
                show("sample.fxml");
                break;
            case "dos":
                show("admin.fxml");
                break;
            default:

                this.panelUserPassIncorrecto.setVisible(true);
                System.out.println("Acceso denegado");
                break;
        }
    }

    //Se cierra el mensaje de usuario o contraseña incorrectos
    public void btnAceptarUserPassIncorrectoActionPerformed(MouseEvent event){
        this.panelUserPassIncorrecto.setVisible(false);
    }


    //Esta función hace el resumen matemático (hash) a la contraseña usando el algoritmo SHA-256
    private String hash(String pass){
        byte[] bytePass = pass.getBytes();
        byte[] passHashed;
        String passHashedValue = "";
        try{
            MessageDigest funcionHash = MessageDigest.getInstance(this.algoritmoHash);
            funcionHash.update(bytePass);
            passHashed = funcionHash.digest();
            passHashedValue = DatatypeConverter.printHexBinary(passHashed);
        }
        catch (Exception e){

        }
        return passHashedValue;
    }

    //Función que muestra una pantalla u otra dependiendo del nivel de privilegios del usuario
    private void show(String path) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.show();
        Stage myStage = (Stage) this.btnAceptar.getScene().getWindow();
        myStage.close();
    }
}
