import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Collection;

public class Helper {

    public void show(String path, AnchorPane parentPane) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(path));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
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
        System.out.println(passHashedValue);
        return passHashedValue;
    }

    public JSONArray GET(String path) throws IOException {
        String ip = "35.239.78.54";
        String puerto = "8080";
        String urlRaiz = "http://" + ip + ":" + puerto;

        String getEndpoint = urlRaiz + path;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(getEndpoint);
        HttpResponse response = httpclient.execute(httpget);

        try{
            //Throw runtime exception if status code isn't 200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
        }
        catch(RuntimeException re){
            return null;
        }
        HttpEntity entity = response.getEntity();

        if(entity != null){
            InputStream instream = entity.getContent();
            String result = convertStreamToString(instream);
            // now you have the string representation of the HTML request
            JSONArray jsonArray = new JSONArray(result);
            instream.close();
            return jsonArray;
        }
        else{
            return null;

        }

    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void setTablaCalificacion(TableView<Calificacion> tabla, String tipo, ObservableList lista, Double anchoCol1, Double anchoCol2){
        tabla.setItems(lista);
        TableColumn<Calificacion, Integer> colCalificacion = new TableColumn<>("Calificación");
        colCalificacion.setCellValueFactory(new PropertyValueFactory<Calificacion, Integer>("calificacion"));
        colCalificacion.setMinWidth(anchoCol1);

        TableColumn<Calificacion, String> colComentario = new TableColumn<>("Comentario");
        colComentario.setCellValueFactory(new PropertyValueFactory<Calificacion, String>("comentario"));
        colComentario.setMinWidth(anchoCol2);
        tabla.getColumns().addAll(colCalificacion, colComentario);
    }

    public void showAlert(String mensaje, Alert.AlertType tipo){
        Alert alert = new Alert(tipo);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
