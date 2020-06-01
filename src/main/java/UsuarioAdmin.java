import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UsuarioAdmin extends Usuario{
    @FXML
    private TextField txtNuevoNombre;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent;

    @FXML
    ImageView btnEditar;

    Helper helper = new Helper();

    private static final String ip = "35.239.78.54";
    private static final String puerto = "8080";
    private static String urlRaiz = "http://" + ip + ":" + puerto;

    @Override
    public void tabComentarios() throws IOException {

        if(entrando){
            String path = "/chef/review/";
            JSONArray jsonArray = helper.GET(path);

            if (jsonArray != null) {
                for (int i=0; i < jsonArray.length(); i++) {
                    listaCalificaciones.add(
                            new Calificacion(
                                    (Integer) jsonArray.getJSONObject(i).get("estrellas"),
                                    (String) jsonArray.getJSONObject(i).get("comentario")
                            )
                    );
                }

                helper.setTablaCalificacion(tableCalificacion, "comentario",
                        listaCalificaciones, Double.parseDouble("80"), Double.parseDouble("270")
                );

                listaRestauranteComent.getItems().add("Servicios Nutresa");
                listaRestauranteComent.getItems().add("Protección");
                listaRestauranteComent.getItems().add("Nacional de Chocolates Bogotá");
                listaRestauranteComent.getItems().add("Suizo");
                listaRestauranteComent.getItems().add("Comercial Nutresa");
                listaRestauranteComent.getItems().add("DHL");
                listaRestauranteComent.getItems().add("Tecnoquímicas San Nicolás");
                listaRestauranteComent.getItems().add("Smurfit casino principal");
                listaRestauranteComent.getItems().add("Smurfit Corrugados");
                listaRestauranteComent.getItems().add("Unilever");
                listaRestauranteComent.getItems().sort((Object c1, Object c2)->{
                    return c1.toString().compareTo((String) c2);
                });
            }
            entrando=false;
        }else{
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
            listaRestauranteComent.getItems().clear();
            entrando=true;
        }
    }
    

    public void tabRestaurantes() throws ClientProtocolException, IOException {
        if(entrando){
            btnEditar.setVisible(false);
            listaRestaurante.getItems().add("Servicios Nutresa");
            listaRestaurante.getItems().add("Protección");
            listaRestaurante.getItems().add("Nacional de Chocolates Bogotá");
            listaRestaurante.getItems().add("Suizo");
            listaRestaurante.getItems().add("Comercial Nutresa");
            listaRestaurante.getItems().add("DHL");
            listaRestaurante.getItems().add("Tecnoquímicas San Nicolás");
            listaRestaurante.getItems().add("Smurfit casino principal");
            listaRestaurante.getItems().add("Smurfit Corrugados");
            listaRestaurante.getItems().add("Unilever");
            this.listaRestaurante.getItems().sort((Object c1, Object c2) -> {
                return c1.toString().compareTo((String) c2);
            });

            entrando = false;
        }
        else{
            listaRestaurante.getItems().clear();
            entrando = true;
        }

    }

    public void ListaRestauranteMouseClicked(MouseEvent event){
        if(listaRestaurante.isFocused()){
            btnEditar.setVisible(true);
        }
    }


    public void cuentaAceptarMouseClicked(MouseEvent event){
        panelConfirmarCuenta.setVisible(true);
    }


    public void cerrarPopupEditarRestaurante(MouseEvent event){
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void editarRestaurante(MouseEvent event){
        this.paneEditarRestaurante.setVisible(true);
        this.listaRestaurante.setDisable(true);
    }

    public void okEditarNombre(MouseEvent event) {
        this.listaRestaurante.getItems().remove(this.listaRestaurante.getSelectionModel().getSelectedItem());
        this.listaRestaurante.getItems().add(txtNuevoNombre.getText());
        this.listaRestaurante.getItems().sort((Object c1, Object c2) -> {
            return c1.toString().compareTo((String) c2);
        });
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void okAgregarRestaurante(MouseEvent event) {
        listaRestaurante.setDisable(false);
        paneAgregarRestaurante.setVisible(false);
    }

    public void cerrarPopupAgregarRestaurante(MouseEvent event) {
        listaRestaurante.setDisable(false);
        paneAgregarRestaurante.setVisible(false);
    }

    public void agregarRestaurante(MouseEvent event) {
        listaRestaurante.setDisable(true);
        paneAgregarRestaurante.setVisible(true);
    }

    public void cerrarPopupCuenta(MouseEvent event) {
        panelConfirmarCuenta.setVisible(false);
    }
}
