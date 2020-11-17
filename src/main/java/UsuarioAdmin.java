import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.passay.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class UsuarioAdmin extends Usuario implements Initializable{
    @FXML
    private TextField txtNuevoNombre, txtNuevoCorreo, txtNuevoRestaurante, txtEditarCorreo2;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante, paneEliminarRestaurante, paneSinPermisos, parentPane;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent;

    @FXML
    private ComboBox comboboxRol;

    @FXML
    private Label labelRestaurantes;

    @FXML
    ImageView btnEditar, btnEliminar, btnAgregar;

    ObservableList<String> listaRoles = FXCollections.observableArrayList();

    @Override
    public void tabComentarios() throws IOException {

    }


    public void tabRestaurantes() throws IOException {
        if(UsuarioEntity.getRol().equals(1)){
            labelRestaurantes.setText("Restaurantes y supervisores");
        }
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


    public void cuentaAceptarMouseClicked(){
        if(!txtNuevoPass.getText().isEmpty()){
            List<Rule> rules = new ArrayList();
            rules.add(new LengthRule(8));
            rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
            rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
            rules.add(new CharacterRule(EnglishCharacterData.Digit, 2));
            rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
            PasswordValidator validator = new PasswordValidator(rules);
            PasswordData password = new PasswordData(txtNuevoPass.getText());
            RuleResult result = validator.validate(password);
            if(!result.isValid()){
                labelCuentaError.setText("La contraseña debe seguir los estandares impuestos");
                paneCuentaError.setVisible(true);
            }
            panelConfirmarCuenta.setVisible(true);
        }else{
            if(!txtCorreo.getText().isEmpty()){
                panelConfirmarCuenta.setVisible(true);
            }else{
                labelCuentaError.setText("No ha introducido ningun valor");
                paneCuentaError.setVisible(true);
            }
        }
    }


    public void cerrarPopupEditarRestaurante(){
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void editarRestaurante(){
        this.paneEditarRestaurante.setVisible(true);
        this.listaRestaurante.setDisable(true);
    }

    public void okEditarRestaurante() throws IOException {
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
        cargarListaUsers();
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
    }

    public void okAgregarRestaurante() {
        Integer rol = 0;
        if(comboboxRol.getValue().toString().equals("Supervisor")){
            rol = 2;
        }
        else if(comboboxRol.getValue().toString().equals("Administrador contrato")){
            rol = 3;
        }
        else{
            helper.showAlert("Debe seleccionar un rol", Alert.AlertType.ERROR);
        }
        try {
            JSONArray jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_ROL,UsuarioEntity.getNombre(), rol.toString()));
            if(jsonArray.getJSONObject(0).get("acceso").equals("false")){
                paneSinPermisos.setVisible(true);
            }
            else{
                rest.POST(
                        routes.getRoute(Routes.routesName.CREATE_USUARIO),
                        "nombre", txtNuevoRestaurante.getText(),
                        "nombreid", txtNuevoRestaurante.getText(),
                        "correo", txtNuevoCorreo.getText(),
                        "password", helper.hash(helper.defaultPass),
                        "rol", rol.toString()
                );
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
                            "id", txtNuevoRestaurante.getText() + diasIterator,
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
            }
        } catch (IOException e) {
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }
        listaRestaurante.setDisable(false);
        paneAgregarRestaurante.setVisible(false);
        txtNuevoRestaurante.setText("");
        txtMostrarBotonAgregar();
        cargarListaUsers();
    }

    public void cerrarPopupAgregarRestaurante() {
        listaRestaurante.setDisable(false);
        paneAgregarRestaurante.setVisible(false);
    }

    public void agregarRestaurante() {
        listaRestaurante.setDisable(true);
        paneAgregarRestaurante.setVisible(true);
    }

    public void eliminarRestaurante() {
        paneEliminarRestaurante.setVisible(true);
    }

    public void okEliminarRestaurante() throws IOException {
        paneEliminarRestaurante.setVisible(false);
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_USUARIO,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_COMENTS,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_DMODEL,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_DHMODEL,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_DISPOMODEL,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        cargarListaUsers();
    }

    public void cerrarPopupEliminarRestaurante() {
        paneEliminarRestaurante.setVisible(false);
    }

    public void txtMostrarBotonAgregar() {
        if(!txtNuevoRestaurante.getText().isEmpty()){
            this.btnAgregar.setVisible(true);
        }
        else{
            this.btnAgregar.setVisible(false);
        }
    }

    public void ListaRestauranteComentMouseClicked() throws IOException {
        if(listaRestauranteComent.isFocused() && listaRestauranteComent.getSelectionModel().getSelectedItem().toString() != ""){
            tableCalificacion.getItems().clear();
            String empresa = listaRestauranteComent.getSelectionModel().getSelectedItem().toString();

            if(empresa.equals("Todos los comentarios")){
                cargarTabla(Routes.routesName.GET_REVIEWS_ADMIN);
            }
            else{
                cargarTabla(Routes.routesName.GET_REVIEWS_USUARIOS, empresa);
            }
        }
    }

    private void cargarDatosTablas(){

    }

    private void cargarListaUsers(){
        listaRestaurante.getItems().clear();
        try{
            TimeUnit.MILLISECONDS.sleep(250);
        }catch (Exception e){
            helper.showAlert("Ocurrió un error inesperado ERR03T", Alert.AlertType.ERROR);//Error 03T error al realizar el delay
        }
        try{
            JSONArray jsonArray2 = null;
            if(UsuarioEntity.getRol().equals(1)){
                jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS));
            }else{
                jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS_ROL3));
            }
            if(jsonArray2 != null){
                for(int i = 0; i < jsonArray2.length(); i++){
                    listaRestaurante.getItems().add((String) jsonArray2.getJSONObject(i).get("nombre"));
                }
                listaRestaurante.getItems().remove((String) "Administrador");
            }else {
                listaRestaurante.setPlaceholder(new Label("No Se Encontraron Restaurantes"));
            }
        }catch(Exception e){
            helper.showAlert("Ocurrió un error al consultar el listado de usuarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
        listaRestaurante.getItems().sort((Object c1, Object c2) -> c1.toString().compareTo((String) c2));
    }

    private void cargarListaComent () {
        /*if(!listaRestauranteComent.getItems().isEmpty()){
            listaRestauranteComent.getItems().clear();
        }*/
        try{
            JSONArray jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS_ROL3));
            if(jsonArray != null ){
                for(int i = 0; i < jsonArray.length(); i++){
                    listaRestauranteComent.getItems().add((String) jsonArray.getJSONObject(i).get("nombre"));
                }
                try{
                    //listaRestaurante.refresh();
                    //listaRestaurante.
                    listaRestauranteComent.getItems().remove((String) "Administrador");
                }
                catch(Exception e){
                    System.out.println("testando");
                }
            }
            listaRestauranteComent.getItems().sort((Object c1, Object c2) -> c1.toString().compareTo((String) c2));
            listaRestauranteComent.getItems().add("Todos los comentarios");
        }catch (IOException e){

        }
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
        listaRoles.add("Supervisor");
        listaRoles.add("Administrador contrato");
        this.comboboxRol.setItems(listaRoles);
        cargarListaUsers();
    }

    public void okSinPermisos() {
        paneSinPermisos.setVisible(false);
    }

    public void btnCerrarSesion(MouseEvent event) throws IOException {
        helper.show("logIn.fxml", parentPane);
        UsuarioEntity.destroy();
    }
}

