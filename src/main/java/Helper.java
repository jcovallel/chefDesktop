import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Helper {
    protected final String defaultPass = "0000";

    public void show(String path, AnchorPane parentPane) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.setTitle("KitchenWorksDesktop");
        stage.getIcons().add(new Image("\\images\\cubiertos.png"));
        stage.show();
        Stage myStage = (Stage) parentPane.getScene().getWindow();
        myStage.close();
    }

    public String hash(String pass){
        String algoritmoHash = "SHA-256";
        byte[] bytePass = pass.getBytes();
        byte[] passHashed;
        String passHashedValue = "";
        try {
            MessageDigest funcionHash = MessageDigest.getInstance(algoritmoHash);
            funcionHash.update(bytePass);
            passHashed = funcionHash.digest();
            passHashedValue = DatatypeConverter.printHexBinary(passHashed);
        } catch (Exception e) {

        }
        return passHashedValue;
    }

    public void setTablaCalificacion(TableView<Calificacion> tabla, ObservableList<Calificacion> lista, Double anchoCol1, Double anchoCol2, Double anchoCol3, Double anchoCol4, Double anchoCol5){

        TableColumn<Calificacion, String> colCalificacion = new TableColumn<>("Calificación");
        colCalificacion.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("cali"));
        colCalificacion.setPrefWidth(anchoCol1);

        TableColumn<Calificacion, String> colComentario = new TableColumn<>("Comentario");
        colComentario.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("come"));
        colComentario.setPrefWidth(anchoCol2);

        TableColumn<Calificacion, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("name"));
        colNombre.setPrefWidth(anchoCol3);

        TableColumn<Calificacion, String> colCelular = new TableColumn<>("Celular");
        colCelular.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("cel"));
        colCelular.setPrefWidth(anchoCol4);

        TableColumn<Calificacion, String> colCorreo = new TableColumn<>("Correo");
        colCorreo.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("addr"));
        tabla.getColumns().addAll(colCalificacion, colComentario, colNombre, colCelular, colCorreo);
        colCorreo.setPrefWidth(anchoCol5);
        tabla.setItems(lista);
    }

    public void showAlert(String mensaje, Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
