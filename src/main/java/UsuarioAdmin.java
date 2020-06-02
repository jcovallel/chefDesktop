

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UsuarioAdmin extends Usuario implements Initializable{
    @FXML
    private TextField txtNuevoNombre, txtNuevoCorreo, txtNuevoRestaurante, txtEditarCorreo2;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante, paneEliminarRestaurante;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent;

    @FXML
    ImageView btnEditar, btnEliminar, btnAgregar;

    private String pathUsuarios = "/chef/getusers/";

    Helper helper = new Helper();
    REST rest = new REST();

    private static final String ip = "35.239.78.54";
    private static final String puerto = "8080";
    private static String urlRaiz = "http://" + ip + ":" + puerto;

    @Override
    public void tabComentarios() throws IOException {

        if(entrando){
            JSONArray jsonArray = rest.GET("/chef/admin/review/");
            System.out.println(jsonArray);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    listaCalificaciones.add(
                            new Calificacion(
                                    jsonArray.getJSONObject(i).get("estrellas").toString(),
                                    (String) jsonArray.getJSONObject(i).get("comentario"),
                                    (String) jsonArray.getJSONObject(i).get("nombre"),
                                    jsonArray.getJSONObject(i).get("celular").toString(),
                                    (String) jsonArray.getJSONObject(i).get("correo")
                            )
                    );
                }
            }
            try{
                jsonArray = rest.GET(pathUsuarios);
                if(jsonArray != null){
                    for(int i = 0; i < jsonArray.length(); i++){
                        listaRestauranteComent.getItems().add((String) jsonArray.getJSONObject(i).get("nombre"));
                    }
                    listaRestauranteComent.getItems().remove((String) "Administrador");
                }
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            this.listaRestaurante.getItems().sort((Object c1, Object c2) -> {
                return c1.toString().compareTo((String) c2);
            });
            listaRestauranteComent.getItems().add("TODOS");
            helper.setTablaCalificacion(
                    tableCalificacion,
                    listaCalificaciones,
                    Double.parseDouble("80"),
                    Double.parseDouble("250"),
                    Double.parseDouble("150"),
                    Double.parseDouble("150"),
                    Double.parseDouble("150")
            );
            entrando=false;
        }else{
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
            listaRestauranteComent.getItems().clear();
            entrando = true;
        }
    }


    public void tabRestaurantes() throws ClientProtocolException, IOException {
        btnEliminar.setVisible(false);
        btnEditar.setVisible(false);
        if(entrando){
            JSONArray jsonArray = rest.GET("/chef/admin/review/");
            System.out.println(jsonArray);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    listaCalificaciones.add(
                            new Calificacion(
                                    jsonArray.getJSONObject(i).get("estrellas").toString(),
                                    (String) jsonArray.getJSONObject(i).get("comentario"),
                                    (String) jsonArray.getJSONObject(i).get("nombre"),
                                    jsonArray.getJSONObject(i).get("celular").toString(),
                                    (String) jsonArray.getJSONObject(i).get("correo")
                            )
                    );
                }
            }
            try{
                jsonArray = rest.GET(pathUsuarios);
                if(jsonArray != null){
                    for(int i = 0; i < jsonArray.length(); i++){
                        listaRestaurante.getItems().add((String) jsonArray.getJSONObject(i).get("nombre"));
                    }
                    listaRestaurante.getItems().remove((String) "Administrador");
                }
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
            this.listaRestaurante.getItems().sort((Object c1, Object c2) -> {
                return c1.toString().compareTo((String) c2);
            });
            entrando=false;
        }else{
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
            listaRestaurante.getItems().clear();
            entrando = true;
        }

    }

    public void ListaRestauranteMouseClicked(MouseEvent event){
        if(listaRestaurante.isFocused() && listaRestaurante.getSelectionModel().getSelectedItem().toString() != ""){
            btnEliminar.setVisible(true);
            btnEditar.setVisible(true);
        }
        else{
            btnEliminar.setVisible(false);
            btnEditar.setVisible(false);
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

    public void okEditarRestaurante(MouseEvent event) throws IOException {
        String nuevoNombre;
        String nuevoCorreo;
        if(txtNuevoNombre.getText().length() <= 0){
            nuevoNombre = "NULL";
        }
        else{
            nuevoNombre = txtNuevoNombre.getText();
            nuevoNombre.replaceAll(" ", "%20");
        }
        if(txtEditarCorreo2.getText().length() <= 0){
            nuevoCorreo = null;
        }
        else{
            nuevoCorreo = txtEditarCorreo2.getText();
            nuevoCorreo.replaceAll(" ", "%20");
        }
        System.out.println(txtEditarCorreo2.getText());

        String path = "/chef/modifyinfoadmi/" + listaRestaurante.getSelectionModel().getSelectedItem() + "/" + nuevoNombre + "/" + nuevoCorreo;
        rest.PUT(path);
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void okAgregarRestaurante(MouseEvent event) {

        try {
            rest.POST(
                    "/chef/createuser/",
                    "nombre", txtNuevoRestaurante.getText(),
                    "nombreid", txtNuevoRestaurante.getText(),
                    "correo", txtNuevoCorreo.getText(),
                    "password", helper.hash(helper.defaultPass));

            String path = "/chef/disponibilidad/" + UsuarioEntity.getNombre();
            rest.PUT(path,
                    "empresaid", txtNuevoRestaurante.getText(),
                    "empresa", txtNuevoRestaurante.getText(),
                    "Lunes", "0",
                    "Martes", "0",
                    "Miercoles", "0",
                    "Jueves", "0",
                    "Viernes", "0"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    public void eliminarRestaurante(MouseEvent event) {
        paneEliminarRestaurante.setVisible(true);
    }

    public void okEliminarRestaurante(MouseEvent event) throws IOException {
        paneEliminarRestaurante.setVisible(false);
        JSONArray jsonArray = null;
        String nombre = (String) listaRestaurante.getSelectionModel().getSelectedItem();
        nombre = nombre.replaceAll(" ", "%20");
        String path = "/chef/deleteuser/" + nombre;
        jsonArray = rest.GET(path);
        if(jsonArray != null){
        }
        else{
        }

    }

    public void cerrarPopupEliminarRestaurante(MouseEvent event) {
        paneEliminarRestaurante.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UsuarioEntity.getUsuario("Administrador");
    }

    public void txtMostrarBotonAgregar(KeyEvent keyEvent) {
        if(this.txtNuevoRestaurante.getText().length() > 0){
            this.btnAgregar.setVisible(true);
        }
        else{
            this.btnAgregar.setVisible(false);
        }
    }

    public void ListaRestauranteComentMouseClicked(MouseEvent event) throws IOException {
        if(listaRestauranteComent.isFocused() && listaRestauranteComent.getSelectionModel().getSelectedItem().toString() != ""){
            tableCalificacion.getItems().clear();
            String empresa = listaRestauranteComent.getSelectionModel().getSelectedItem().toString();
            System.out.println(empresa);
            empresa = empresa.replaceAll(" ", "%20");

            String path = "/chef/user/review/" + empresa;
            if(empresa.equals("Administrador")){
                path = "/chef/admin/review/";
            }
            JSONArray jsonArray = rest.GET(path);
            listaCalificaciones.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                listaCalificaciones.add(
                        new Calificacion(
                                jsonArray.getJSONObject(i).get("estrellas").toString(),
                                (String) jsonArray.getJSONObject(i).get("comentario"),
                                (String) jsonArray.getJSONObject(i).get("nombre"),
                                jsonArray.getJSONObject(i).get("celular").toString(),
                                (String) jsonArray.getJSONObject(i).get("correo")
                        )
                );
            }
            helper.setTablaCalificacion(
                    tableCalificacion,
                    listaCalificaciones,
                    Double.parseDouble("80"),
                    Double.parseDouble("250"),
                    Double.parseDouble("150"),
                    Double.parseDouble("150"),
                    Double.parseDouble("150")
            );
        }
    }

}

