
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class UsuarioAdmin extends Usuario implements Initializable{
    @FXML
    private TextField txtNuevoNombre, txtNuevoCorreo, txtNuevoRestaurante, txtEditarCorreo2;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante, paneEliminarRestaurante;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent;

    @FXML
    ImageView btnEditar, btnEliminar, btnAgregar;

    @Override
    public void tabComentarios() throws IOException {

    }


    public void tabRestaurantes() throws IOException {
        btnEliminar.setVisible(false);
        btnEditar.setVisible(false);
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
        }
        if(txtEditarCorreo2.getText().length() <= 0){
            nuevoCorreo = null;
        }
        else{
            nuevoCorreo = txtEditarCorreo2.getText();
        }

        rest.PUT(
                routes.getRoute(
                        Routes.routesName.MODIFY_RESTAURANTE,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString(),
                        nuevoNombre,
                        nuevoCorreo
                )
        );
        cargarLista();
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void okAgregarRestaurante(MouseEvent event) {

        try {
            rest.POST(
                    routes.getRoute(Routes.routesName.CREATE_USUARIO),
                    "nombre", txtNuevoRestaurante.getText(),
                    "nombreid", txtNuevoRestaurante.getText(),
                    "correo", txtNuevoCorreo.getText(),
                    "password", helper.hash(helper.defaultPass));
            rest.PUT(
                    routes.getRoute(
                            Routes.routesName.MODIFY_DISPONIBILIDAD,
                            UsuarioEntity.getNombre()
                    ),
                    "empresaid", txtNuevoRestaurante.getText(),
                    "empresa", txtNuevoRestaurante.getText(),
                    "Lunes", "0",
                    "Martes", "0",
                    "Miercoles", "0",
                    "Jueves", "0",
                    "Viernes", "0"
            );
            String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
            for(String diasIterator : dias){
                rest.POST(
                        routes.getRoute(Routes.routesName.CREATE_HOURS),
                        "empresa", txtNuevoRestaurante.getText(),
                        "dia", diasIterator,
                        "franja1", "0",
                        "franja2", "0",
                        "franja3", "0",
                        "franja4", "0",
                        "franja5", "0",
                        "franja6", "0",
                        "franja7", "0",
                        "franja8", "0",
                        "franja9", "0",
                        "franja10", "0",
                        "franja11", "0",
                        "franja12", "0",
                        "franja13", "0",
                        "franja14", "0",
                        "franja15", "0",
                        "franja16", "0",
                        "franja17", "0",
                        "franja18", "0",
                        "franja19", "0",
                        "franja20", "0"
                );
            }
        } catch (IOException e) {
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }

        cargarLista();
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
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_USUARIO,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        cargarLista();
    }

    public void cerrarPopupEliminarRestaurante(MouseEvent event) {
        paneEliminarRestaurante.setVisible(false);
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

            if(empresa.equals("Administrador")){
                cargarTabla(Routes.routesName.GET_REVIEWS_ADMIN);
            }
            else{
                cargarTabla(Routes.routesName.GET_REVIEWS_USUARIOS, empresa);
            }
        }
    }

    private void cargarDatosTablas(){

    }

    private void cargarLista(){
        if(!listaRestaurante.getItems().isEmpty()){
            listaRestaurante.getItems().clear();
        }
        if(!listaRestauranteComent.getItems().isEmpty()){
            listaRestauranteComent.getItems().clear();
        }
        JSONArray jsonArray;
        try{
            jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS));
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    listaRestaurante.getItems().add((String) jsonArray.getJSONObject(i).get("nombre"));
                    listaRestauranteComent.getItems().add((String) jsonArray.getJSONObject(i).get("nombre"));
                }
                listaRestaurante.getItems().remove((String) "Administrador");
                listaRestauranteComent.getItems().remove((String) "Administrador");
            }
        }
        catch(IOException ioe){
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }
        listaRestaurante.getItems().sort((Object c1, Object c2) -> {
            return c1.toString().compareTo((String) c2);
        });
        listaRestauranteComent.getItems().sort((Object c1, Object c2) -> {
            return c1.toString().compareTo((String) c2);
        });
        listaRestauranteComent.getItems().add("Administrador");
    }

    protected void cargarTabla(Routes.routesName route, String... args) throws IOException {

        tableCalificacion.getItems().clear();
        tableCalificacion.getColumns().clear();

        JSONArray jsonArray = rest.GET(routes.getRoute(route, args));
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarLista();
    }
}

