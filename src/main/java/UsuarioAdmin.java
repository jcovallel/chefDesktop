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
import org.apache.commons.validator.routines.EmailValidator;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class UsuarioAdmin extends Usuario implements Initializable{
    @FXML
    private TextField txtNuevoNombre, txtNuevoCorreo1, txtNuevoRestaurante, txtEditarCorreo, txtNuevoTmenu, txtNuevoNombreTmenu;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante1, paneAgregarRestaurante2, paneEliminarRestaurante, paneSinPermisos, parentPane,
            paneEliminarTmenu, paneAgregarTmenu, paneEditarTmenu, paneAsignarMenu;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent, listaTmenu;

    @FXML
    private ComboBox comboboxRol;

    @FXML
    private Label labelRestaurantes, labelAgregarMenu;

    @FXML
    ImageView btnEditar, btnEliminar, btnAgregar, btnEliminarTmenu, btnAgregarTmenu, btnEditarTmenu;

    ObservableList<String> listaRoles = FXCollections.observableArrayList();

    String listviewVacia;

    @Override
    public void tabComentarios() {

    }

    public void tabRestaurantes() {
        if(UsuarioEntity.getRol().equals(1)){
            labelRestaurantes.setText("Restaurantes y Supervisores");
            txtNuevoRestaurante.setPromptText("Escriba el nombre del restaurante o supervisor a añadir");
        }
        btnEliminar.setVisible(false);
        btnEditar.setVisible(false);
    }

    public void tabTmenu() {
        cargarListaMenus();
        btnEliminarTmenu.setVisible(false);
        btnEditarTmenu.setVisible(false);
    }

    //################################################### METODOS PESTAÑA RESTAURANTE ##############################################################
    //CUANDO SE CLICKEA Y SELECCIONA UN ELEMENTO DE LA LISTVIEW (RETAURANTE O SUEPRVISOR)
    public void ListaRestauranteMouseClicked(){
        try{
            if(listaRestaurante.isFocused() && listaRestaurante.getSelectionModel().getSelectedItem().toString() != ""){
                btnEliminar.setVisible(true);
                btnEditar.setVisible(true);
            }
            else{
                btnEliminar.setVisible(false);
                btnEditar.setVisible(false);
            }
        }catch (NullPointerException e){

        }
    }

    //ACTUALIZA LA LISTA DE SUPERVISORES Y RESTAURANTES
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
                if(jsonArray2.length()==1){
                    listaRestaurante.setPlaceholder(new Label(listviewVacia));
                    listaRestaurante.setDisable(true);
                }else {
                    listaRestaurante.setDisable(false);
                    for(int i = 0; i < jsonArray2.length(); i++){
                        listaRestaurante.getItems().add((String) jsonArray2.getJSONObject(i).get("nombre"));
                    }
                    listaRestaurante.getItems().remove((String) "Administrador");
                }
            }else {
                helper.showAlert("Ocurrió un error al consultar el listado de usuarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            }
        }catch(Exception e){
            helper.showAlert("Ocurrió un error al consultar el listado de usuarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
        listaRestaurante.getItems().sort((Object c1, Object c2) -> c1.toString().compareTo((String) c2));
    }

    //===================================================== METODOS DE EDICION RESTAURANTES =======================================================
    //CUANDO SE SELECCIONA LA OPCION EDITAR RESTAURANTE
    public void editarRestaurante(){
        this.paneEditarRestaurante.setVisible(true);
        this.listaRestaurante.setDisable(true);
        this.txtNuevoRestaurante.setDisable(true);
        this.btnAgregar.setVisible(false);
    }

    //MUESTRA EL BOTON DE AGREGAR CUANDO SE ESCRIBE TEXTO EN EL CUADRO DE NOMBRE PARA AGREGAR RESTAURANTE
    public void txtMostrarBotonAgregar() {
        if(txtNuevoRestaurante.getCharacters().length()!=0){
            this.btnAgregar.setVisible(true);
        }
        else{
            this.btnAgregar.setVisible(false);
        }
    }

    //CUANDO SE CIERRA LA VENTANA DE EDITAR RESTAURANTE
    public void cerrarPopupEditarRestaurante(){
        this.paneEditarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
        this.txtNuevoRestaurante.setDisable(false);
        txtMostrarBotonAgregar();
        txtNuevoNombre.setText("");
        txtEditarCorreo.setText("");
    }

    //CUANDO SE DA OK Y SE REALIZA LA EDICION DEL RESTAURANTE
    public void okEditarRestaurante() throws IOException {
        String nuevoNombre;
        String nuevoCorreo;
        int ambosNULL = 0;
        boolean badMail = false;
        if(txtNuevoNombre.getText().length() <= 0){
            nuevoNombre = "NULL";
            ambosNULL++;
        }else{
            nuevoNombre = txtNuevoNombre.getText();
        }

        if(txtEditarCorreo.getText().length() <= 0){
            nuevoCorreo = "NULL";
            ambosNULL++;
        }else{
            nuevoCorreo = txtEditarCorreo.getText();
            if(!EmailValidator.getInstance().isValid(nuevoCorreo)){
                helper.showAlert("Debe proporcionar una direccion de correo electronico valida", Alert.AlertType.ERROR);
                badMail=true;
            }
        }

        if(ambosNULL!=2 && badMail!=true){
            rest.PUT(
                    routes.getRoute(
                            Routes.routesName.MODIFY_RESTAURANTE,
                            listaRestaurante.getSelectionModel().getSelectedItem().toString(),
                            nuevoNombre,
                            nuevoCorreo
                    )
            );
        }
        if(badMail!=true){
            cargarListaUsers();
            this.paneEditarRestaurante.setVisible(false);
            this.listaRestaurante.setDisable(false);
            this.txtNuevoRestaurante.setDisable(false);
            txtMostrarBotonAgregar();
            txtNuevoNombre.setText("");
            txtEditarCorreo.setText("");
        }
    }
    //===============================================================================================================================================

    //======================================================== METODOS AGREGAR RESTAURANTES =========================================================
    //CUANDO SE HACE CLICK EN OK AGREGAR RESTAURANTE
    public void okAgregarRestaurante() {
        Boolean rolVacio = false;
        ListaRestauranteMouseClicked();
        if(txtNuevoCorreo1.getText().isEmpty()){
            helper.showAlert("Debe proporcionar un correo electronico", Alert.AlertType.ERROR);
        }else if(!EmailValidator.getInstance().isValid(txtNuevoCorreo1.getText())){
            helper.showAlert("Debe proporcionar una direccion de correo electronico valida", Alert.AlertType.ERROR);
        }else {
            try{
                Integer rol = 0;
                if(comboboxRol.getValue().toString().equals("Supervisor")){
                    rol = 2;
                }else if(comboboxRol.getValue().toString().equals("Administrador contrato")){
                    rol = 3;
                }

                try {
                    String respuesta;
                    respuesta = rest.POST(
                            routes.getRoute(Routes.routesName.CREATE_USUARIO),
                            "id", txtNuevoRestaurante.getText(),
                            "nombre", txtNuevoRestaurante.getText(),
                            "correo", txtNuevoCorreo1.getText(),
                            "password", helper.hash(helper.defaultPass),
                            "rol", rol.toString()
                    );

                    if(!respuesta.equals("sucess")){
                        if(respuesta.contains("duplicate key error")){
                            helper.showAlert("Error: Nombre ya registrado", Alert.AlertType.ERROR);
                        }else{
                            helper.showAlert("Ocurrió un error al registrar el sitio, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                        }
                    }

                /*rest.PUT(
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
                }*/
                } catch (Exception e) {
                    helper.showAlert("Ocurrió un error al registrar el sitio, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                }

            }catch (NullPointerException e){
                helper.showAlert("Debe seleccionar un rol", Alert.AlertType.ERROR);
                rolVacio=true;
            }
            if(!rolVacio){
                listaRestaurante.setDisable(false);
                paneAgregarRestaurante1.setVisible(false);
                paneAgregarRestaurante2.setVisible(false);
                txtNuevoRestaurante.setText("");
                txtNuevoRestaurante.setDisable(false);
                txtNuevoCorreo1.setText("");
                comboboxRol.valueProperty().set(null);
                txtMostrarBotonAgregar();
                cargarListaUsers();
            }
        }
    }

    //CUANDO SE HACE CLICK EN EL BOTON +(MÁS) PARA AGREGAR RESTAURANTE
    public void agregarRestaurante() {
        listaRestaurante.setDisable(true);
        this.txtNuevoRestaurante.setDisable(true);
        this.btnAgregar.setVisible(false);
        if(UsuarioEntity.getRol()==1){
            paneAgregarRestaurante1.setVisible(true);
        }else {
            paneAgregarRestaurante2.setVisible(true);
        }
    }

    //CUANDO SE CIERRA LA VENTANA DE AGREGAR RESTAURANTE
    public void cerrarPopupAgregarRestaurante() {
        cargarListaUsers();
        this.txtNuevoRestaurante.setDisable(false);
        this.btnAgregar.setVisible(true);
        txtNuevoCorreo1.setText("");
        comboboxRol.valueProperty().set(null);
        paneAgregarRestaurante1.setVisible(false);
        paneAgregarRestaurante2.setVisible(false);
    }
    //===============================================================================================================================================

    //======================================================== METODOS ELIMINAR RESTAURANTES =========================================================
    //CUANDO SE HACE CLICK EN EL BOTON DE ELIMINAR RESTAURANTE
    public void eliminarRestaurante() {
        paneEliminarRestaurante.setVisible(true);
        this.listaRestaurante.setDisable(true);
        this.txtNuevoRestaurante.setDisable(true);
        this.btnAgregar.setVisible(false);
    }

    //CUANDO SE HACE CLICK EN OK EN LA VENTANA DE ELIMINAR RESTAURANTE
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
        this.txtNuevoRestaurante.setDisable(false);
        txtMostrarBotonAgregar();
    }

    //CUANDO SE HACE CIERRA LA VENTANA DE ELIMINAR RESTAURANTE
    public void cerrarPopupEliminarRestaurante() {
        paneEliminarRestaurante.setVisible(false);
        this.listaRestaurante.setDisable(false);
        this.txtNuevoRestaurante.setDisable(false);
        txtMostrarBotonAgregar();
    }
    //===============================================================================================================================================
    //###############################################################################################################################################

    //######################################################## METODOS PESTAÑA COMENTARIOS ###########################################################

    //###############################################################################################################################################

    //################################################### METODOS PESTAÑA MENU ##############################################################
    //CUANDO SE CLICKEA Y SELECCIONA UN ELEMENTO DE LA LISTVIEW (MENU)
    public void ListaTmenuMouseClicked(){
        try{
            if(listaTmenu.isFocused() && listaTmenu.getSelectionModel().getSelectedItem().toString() != ""){
                btnEliminarTmenu.setVisible(true);
                btnEditarTmenu.setVisible(true);
            }
            else{
                btnEliminarTmenu.setVisible(false);
                btnEditarTmenu.setVisible(false);
            }
        }catch (NullPointerException e){

        }
    }

    //ACTUALIZA LA LISTA DE SUPERVISORES Y RESTAURANTES
    private void cargarListaMenus(){
        listaTmenu.getItems().clear();
        try{
            TimeUnit.MILLISECONDS.sleep(250);
        }catch (Exception e){
            helper.showAlert("Ocurrió un error inesperado ERR03T", Alert.AlertType.ERROR);//Error 03T error al realizar el delay
        }
        try{
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS));
            if(jsonArray2 != null){
                listaTmenu.setDisable(false);
                for(int i = 0; i < jsonArray2.length(); i++){
                    listaTmenu.getItems().add((String) jsonArray2.getJSONObject(i).get("menu"));
                }
            }else {
                listaTmenu.setPlaceholder(new Label("No se encontraron menus"));
                listaTmenu.setDisable(true);
            }
        }catch(Exception e){
            helper.showAlert("Ocurrió un error al consultar el listado de menus, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
        listaTmenu.getItems().sort((Object c1, Object c2) -> c1.toString().compareTo((String) c2));
    }

    //===================================================== METODOS DE EDICION MENUS ============================================================
    //CUANDO SE SELECCIONA LA OPCION EDITAR MENU
    public void editarTmenu(){
        this.paneEditarTmenu.setVisible(true);
        this.listaTmenu.setDisable(true);
        this.txtNuevoTmenu.setDisable(true);
        this.btnAgregarTmenu.setVisible(false);
    }

    //MUESTRA EL BOTON DE AGREGAR CUANDO SE ESCRIBE TEXTO EN EL CUADRO DE NOMBRE PARA AGREGAR MENU
    public void txtMostrarBotonAgregarTmenu() {
        if(txtNuevoTmenu.getCharacters().length()!=0){
            this.btnAgregarTmenu.setVisible(true);
        }
        else{
            this.btnAgregarTmenu.setVisible(false);
        }
    }

    //CUANDO SE CIERRA LA VENTANA DE EDITAR MENU
    public void cerrarPopupEditarTmenu(){
        this.paneEditarTmenu.setVisible(false);
        this.listaTmenu.setDisable(false);
        this.txtNuevoTmenu.setDisable(false);
        txtMostrarBotonAgregarTmenu();
        txtNuevoNombreTmenu.setText("");
    }

    //CUANDO SE DA OK Y SE REALIZA LA EDICION DEL MENU
    public void okEditarTmenu() throws IOException {
        String nuevoNombreTmenu;
        int nombreNULL = 0;
        if(txtNuevoNombreTmenu.getText().length() <= 0){
            nuevoNombreTmenu = "NULL";
            nombreNULL++;
        }else{
            nuevoNombreTmenu = txtNuevoNombreTmenu.getText();
        }

        if(nombreNULL==0){
            rest.PUT(
                    routes.getRoute(
                            Routes.routesName.MODIFY_MENU,
                            listaTmenu.getSelectionModel().getSelectedItem().toString(),
                            nuevoNombreTmenu
                    )
            );
        }
        cargarListaMenus();
        this.paneEditarTmenu.setVisible(false);
        this.listaTmenu.setDisable(false);
        this.txtNuevoTmenu.setDisable(false);
        txtMostrarBotonAgregarTmenu();
        txtNuevoNombreTmenu.setText("");
    }
    //===============================================================================================================================================

    //======================================================== METODOS AGREGAR MENUs =========================================================
    //CUANDO SE HACE CLICK EN OK AGREGAR RESTAURANTE
    public void okAgregarTmenu() {
        ListaTmenuMouseClicked();
        try {
            String respuesta;
            respuesta = rest.POST(
                    routes.getRoute(Routes.routesName.CREATE_MENU),
                    "id", txtNuevoTmenu.getText(),
                    "menu", txtNuevoTmenu.getText()
            );

            if(!respuesta.equals("sucess")){
                if(respuesta.contains("duplicate key error")){
                    helper.showAlert("Error: Menú ya registrado", Alert.AlertType.ERROR);
                }else{
                    helper.showAlert("Ocurrió un error al agregar el menú, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            helper.showAlert("Ocurrió un error al agregar el menú, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
        listaTmenu.setDisable(false);
        paneAgregarTmenu.setVisible(false);
        txtNuevoTmenu.setText("");
        txtNuevoTmenu.setDisable(false);
        txtMostrarBotonAgregarTmenu();
        cargarListaMenus();
    }

    //CUANDO SE HACE CLICK EN EL BOTON +(MÁS) PARA AGREGAR MENU
    public void agregarTmenu() {
        listaTmenu.setDisable(true);
        this.txtNuevoTmenu.setDisable(true);
        this.btnAgregarTmenu.setVisible(false);
        labelAgregarMenu.setText("Desea agregar \""+txtNuevoTmenu.getText().toString()+"\" al listado de menus?");
        paneAgregarTmenu.setVisible(true);
    }

    //CUANDO SE CIERRA LA VENTANA DE AGREGAR RESTAURANTE
    public void cerrarPopupAgregarTmenu() {
        cargarListaMenus();
        this.txtNuevoTmenu.setDisable(false);
        this.btnAgregarTmenu.setVisible(true);
        paneAgregarTmenu.setVisible(false);
    }
    //===============================================================================================================================================

    //======================================================== METODOS ELIMINAR RESTAURANTES =========================================================
    //CUANDO SE HACE CLICK EN EL BOTON DE ELIMINAR RESTAURANTE
    public void eliminarTmenu() {
        paneEliminarTmenu.setVisible(true);
        this.listaTmenu.setDisable(true);
        this.txtNuevoTmenu.setDisable(true);
        this.btnAgregarTmenu.setVisible(false);
    }

    //CUANDO SE HACE CLICK EN OK EN LA VENTANA DE ELIMINAR RESTAURANTE
    public void okEliminarTmenu() throws IOException {
        paneEliminarTmenu.setVisible(false);
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_MENU,
                        listaTmenu.getSelectionModel().getSelectedItem().toString()
                )
        );
        cargarListaMenus();
        this.txtNuevoTmenu.setDisable(false);
        txtMostrarBotonAgregarTmenu();
    }

    //CUANDO SE HACE CIERRA LA VENTANA DE ELIMINAR RESTAURANTE
    public void cerrarPopupEliminarTmenu() {
        paneEliminarTmenu.setVisible(false);
        this.listaTmenu.setDisable(false);
        this.txtNuevoTmenu.setDisable(false);
        txtMostrarBotonAgregarTmenu();
    }
    //===============================================================================================================================================
    //###############################################################################################################################################

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
        listaRoles.add("Administrador contrato");
        listviewVacia="No Se Encontraron Restaurantes";
        if(UsuarioEntity.getRol()==1){
            listaRoles.add("Supervisor");
            listviewVacia="No Se Encontraron Restaurantes o Supervisores";
        }
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

