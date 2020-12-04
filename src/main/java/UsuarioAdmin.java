import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.passay.*;
import org.apache.commons.validator.routines.EmailValidator;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class UsuarioAdmin extends Usuario implements Initializable{
    @FXML
    private TextField txtNuevoNombre, txtNuevoCorreo1, txtNuevoCorreo2, txtNuevoRestaurante, txtEditarCorreo, txtNuevoTmenu, txtNuevoNombreTmenu;

    @FXML
    private AnchorPane paneEditarRestaurante, paneAgregarRestaurante1, paneAgregarRestaurante2, paneEliminarRestaurante, paneSinPermisos, parentPane,
            paneEliminarTmenu, paneAgregarTmenu, paneEditarTmenu, paneAsignarMenu, reservaAnchor, paneDispoSuccess, paneAsignarMenuSuccess;

    @FXML
    private ListView listaRestaurante, listaRestauranteComent, listaTmenu;

    @FXML
    private ComboBox comboboxRol, horaInicioReserva, minutoInicioReserva, amInicioReserva, horaFinReserva,
            minutoFinReserva, amFinReserva, horaInicioEntrega, minutoInicioEntrega,amInicioEntrega, horaFinEntrega,
            minutoFinEntrega, amFinEntrega, ListaRestaurant, ListaMenuReservas;

    @FXML
    private Label labelRestaurantes, labelAgregarMenu, cambioHorarioTxt, cambioDispoTxt;

    @FXML
    ImageView btnEditar, btnEliminar, btnAgregar, btnEliminarTmenu, btnAgregarTmenu, btnEditarTmenu, cambioHorarioImg,
            cambioDispoImg;

    @FXML
    private CheckBox checkLunes, checkMartes, checkMiercoles, checkJueves, checkViernes, checkSabado, checkDomingo;

    @FXML
    private Button btnSelectMenu;

    @FXML
    private Tab reservaTab;

    @FXML
    TableView tableview;

    TableColumn checkCol = new TableColumn();
    TableColumn nombreMenuCol = new TableColumn();

    ObservableList<String> listaRoles = FXCollections.observableArrayList();

    ObservableList<MenuModel> listaMenus = FXCollections.observableArrayList();

    String listviewVacia;

    @Override
    public void tabComentarios() {

    }

    public void tabRestaurantes() {
        if(UsuarioEntity.getRol().equals(1)){
            labelRestaurantes.setText("Restaurantes y Supervisores");
            txtNuevoRestaurante.setPromptText("Escriba el nombre del restaurante o supervisor a añadir");
        }else{
            txtNuevoRestaurante.setPromptText("Escriba el nombre del restaurante a añadir");
        }
        btnEliminar.setVisible(false);
        btnEditar.setVisible(false);
        btnSelectMenu.setVisible(false);
    }

    public void tabTmenu() {
        cargarListaMenus();
        btnEliminarTmenu.setVisible(false);
        btnEditarTmenu.setVisible(false);
    }

    public void tabReserva(){
        if(reservaTab.isSelected()){
            ObservableList<String> listaResta = FXCollections.observableArrayList();
            try{
                JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS_ROL3));
                if(jsonArray2 != null && !jsonArray2.isEmpty()){
                    reservaAnchor.setDisable(false);
                    ListaMenuReservas.setDisable(true);
                    horaInicioReserva.setDisable(true);
                    minutoInicioReserva.setDisable(true);
                    amInicioReserva.setDisable(true);
                    horaFinReserva.setDisable(true);
                    minutoFinReserva.setDisable(true);
                    amFinReserva.setDisable(true);
                    horaInicioEntrega.setDisable(true);
                    minutoInicioEntrega.setDisable(true);
                    amInicioEntrega.setDisable(true);
                    horaFinEntrega.setDisable(true);
                    minutoFinEntrega.setDisable(true);
                    amFinEntrega.setDisable(true);
                    cambioHorarioImg.setDisable(true);
                    cambioHorarioTxt.setDisable(true);
                    checkLunes.setDisable(true);
                    checkMartes.setDisable(true);
                    checkMiercoles.setDisable(true);
                    checkJueves.setDisable(true);
                    checkViernes.setDisable(true);
                    checkSabado.setDisable(true);
                    checkDomingo.setDisable(true);
                    cambioDispoImg.setDisable(true);
                    cambioDispoTxt.setDisable(true);

                    for(int i = 0; i < jsonArray2.length(); i++){
                        listaResta.add((String) jsonArray2.getJSONObject(i).get("nombre"));
                    }
                    this.ListaRestaurant.setItems(listaResta);
                }else {
                    reservaAnchor.setDisable(true);
                }
            }catch (Exception e){

            }
        }else {
            this.ListaRestaurant.getSelectionModel().clearSelection();
            checkLunes.setSelected(false);
            checkMartes.setSelected(false);
            checkMiercoles.setSelected(false);
            checkJueves.setSelected(false);
            checkViernes.setSelected(false);
            checkSabado.setSelected(false);
            checkDomingo.setSelected(false);
            this.ListaMenuReservas.getSelectionModel().clearSelection();
            horaInicioReserva.getSelectionModel().clearSelection();
            minutoInicioReserva.getSelectionModel().clearSelection();
            amInicioReserva.getSelectionModel().clearSelection();
            horaFinReserva.getSelectionModel().clearSelection();
            minutoFinReserva.getSelectionModel().clearSelection();
            amFinReserva.getSelectionModel().clearSelection();
            horaInicioEntrega.getSelectionModel().clearSelection();
            minutoInicioEntrega.getSelectionModel().clearSelection();
            amInicioEntrega.getSelectionModel().clearSelection();
            horaFinEntrega.getSelectionModel().clearSelection();
            minutoFinEntrega.getSelectionModel().clearSelection();
            amFinEntrega.getSelectionModel().clearSelection();
        }
    }

    //################################################### METODOS PESTAÑA RESTAURANTE ##############################################################
    //CUANDO SE CLICKEA Y SELECCIONA UN ELEMENTO DE LA LISTVIEW (RETAURANTE O SUEPRVISOR)
    public void ListaRestauranteMouseClicked(){
        try{
            if(listaRestaurante.isFocused() && listaRestaurante.getSelectionModel().getSelectedItem().toString() != ""){
                btnEliminar.setVisible(true);
                btnEditar.setVisible(true);
                btnSelectMenu.setVisible(true);
            }
            else{
                btnEliminar.setVisible(false);
                btnEditar.setVisible(false);
                btnSelectMenu.setVisible(false);
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
                if(jsonArray2.length()==1 && UsuarioEntity.getRol().equals(1)){
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

        if(txtNuevoCorreo1.getText().isEmpty() && txtNuevoCorreo2.getText().isEmpty()){
            helper.showAlert("Debe proporcionar un correo electronico", Alert.AlertType.ERROR);
        }else if(!EmailValidator.getInstance().isValid(txtNuevoCorreo1.getText()) && !EmailValidator.getInstance().isValid(txtNuevoCorreo2.getText())){
            helper.showAlert("Debe proporcionar una direccion de correo electronico valida", Alert.AlertType.ERROR);
        }else {
            try{
                Integer rol = 0;
                if(UsuarioEntity.getRol()==1){
                    if(comboboxRol.getValue().toString().equals("Supervisor")){
                        rol = 2;
                    }else if(comboboxRol.getValue().toString().equals("Administrador contrato")){
                        rol = 3;
                    }
                }else{
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
                    }else {
                        if(rol==3){
                            //CREACION MENU EMPRESA
                            JSONArray jarray = new JSONArray();
                            try{
                                JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS));
                                if(jsonArray2 != null){
                                    listaTmenu.setDisable(false);
                                    for(int i = 0; i < jsonArray2.length(); i++){
                                        JSONObject jObject = new JSONObject();
                                        String menu = (String) jsonArray2.getJSONObject(i).get("menu");
                                        jObject.put("id",txtNuevoRestaurante.getText()+menu);
                                        jObject.put("empresa",txtNuevoRestaurante.getText());
                                        jObject.put("menu",menu);
                                        jObject.put("check",false);
                                        jarray.put(jObject);
                                        // creacion disponibilidad menu
                                        rest.POST(
                                                routes.getRoute(Routes.routesName.CREATE_DISPO_MENU),
                                                "id", txtNuevoRestaurante.getText()+menu,
                                                "empresa", txtNuevoRestaurante.getText(),
                                                "menu", menu,
                                                "lunesref", "0",
                                                "martesref", "0",
                                                "miercolesref", "0",
                                                "juevesref", "0",
                                                "vienesref", "0",
                                                "sabadoref", "0",
                                                "domingoref", "0",
                                                "lunes", "0",
                                                "martes", "0",
                                                "miercoles", "0",
                                                "jueves", "0",
                                                "viernes", "0",
                                                "sabado", "0",
                                                "domingo", "0"
                                        );
                                    }
                                    rest.POSTARRAY(routes.getRoute(Routes.routesName.CREATE_MENU_EMPRESA), jarray);
                                }else {
                                    //listaTmenu.setPlaceholder(new Label("No se encontraron menus"));
                                    //listaTmenu.setDisable(true);
                                }
                            }catch(Exception e){
                                helper.showAlert("Ocurrió un error al consultar el listado de menus, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                            }
                            //DISPONIBILIDAD DIARIA DEL RESTAURANTE
                            rest.POST(
                                    routes.getRoute(Routes.routesName.CREATE_DIAS_DISPONIBLES_EMPRESA),
                                    "id", txtNuevoRestaurante.getText(),
                                    "nombre", txtNuevoRestaurante.getText(),
                                    "lunes", "false",
                                    "martes", "false",
                                    "miercoles", "false",
                                    "jueves", "false",
                                    "viernes", "false",
                                    "sabado", "false",
                                    "domingo", "false"
                            );

                            //DISPONIBILIDAD FRANJAS HORARIAS
                            String[] dias = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};
                            for(String diasIterator : dias){
                                rest.POST(
                                        routes.getRoute(Routes.routesName.CREATE_FRANJA_HORAS),
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
                                        "franja20", "0",
                                        "franja21", "0",
                                        "franja22", "0",
                                        "franja23",	"0",
                                        "franja24",	"0",
                                        "franja25",	"0",
                                        "franja26",	"0",
                                        "franja27",	"0",
                                        "franja28",	"0",
                                        "franja29",	"0",
                                        "franja30",	"0",
                                        "franja31",	"0",
                                        "franja32",	"0",
                                        "franja33",	"0",
                                        "franja34",	"0",
                                        "franja35",	"0",
                                        "franja36",	"0",
                                        "franja37",	"0",
                                        "franja38",	"0",
                                        "franja39",	"0",
                                        "franja40",	"0",
                                        "franja41",	"0",
                                        "franja42",	"0",
                                        "franja43",	"0",
                                        "franja44",	"0",
                                        "franja45",	"0",
                                        "franja46",	"0",
                                        "franja47",	"0",
                                        "franja48",	"0",
                                        "franja49",	"0",
                                        "franja50",	"0",
                                        "franja51",	"0",
                                        "franja52",	"0",
                                        "franja53",	"0",
                                        "franja54",	"0",
                                        "franja55",	"0",
                                        "franja56",	"0",
                                        "franja57",	"0",
                                        "franja58",	"0",
                                        "franja59",	"0",
                                        "franja60",	"0",
                                        "franja61",	"0",
                                        "franja62",	"0",
                                        "franja63",	"0",
                                        "franja64",	"0",
                                        "franja65",	"0",
                                        "franja66",	"0",
                                        "franja67",	"0",
                                        "franja68",	"0",
                                        "franja69",	"0",
                                        "franja70",	"0",
                                        "franja71",	"0",
                                        "franja72",	"0",
                                        "franja73",	"0",
                                        "franja74",	"0",
                                        "franja75",	"0",
                                        "franja76",	"0",
                                        "franja77",	"0",
                                        "franja78",	"0",
                                        "franja79",	"0",
                                        "franja80",	"0",
                                        "franja81",	"0",
                                        "franja82",	"0",
                                        "franja83",	"0",
                                        "franja84",	"0",
                                        "franja85",	"0",
                                        "franja86",	"0",
                                        "franja87",	"0",
                                        "franja88",	"0",
                                        "franja89",	"0",
                                        "franja90",	"0",
                                        "franja91",	"0",
                                        "franja92",	"0",
                                        "franja93",	"0",
                                        "franja94",	"0",
                                        "franja95",	"0",
                                        "franja96",	"0"
                                );
                            }
                        }
                    }
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
                        Routes.routesName.DELETE_DISPO_DIAS,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_HORARIO_MENU,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_LISMENU_EMPRESAS,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        rest.GET(
                routes.getRoute(
                        Routes.routesName.DELETE_DISPO_MENU,
                        listaRestaurante.getSelectionModel().getSelectedItem().toString()
                )
        );
        /*rest.GET(
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
        );*/
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

    //ACTUALIZA LA LISTA DE MENUS
    private void cargarListaMenus(){
        listaTmenu.getItems().clear();
        try{
            TimeUnit.MILLISECONDS.sleep(250);
        }catch (Exception e){
            helper.showAlert("Ocurrió un error inesperado ERR03T", Alert.AlertType.ERROR);//Error 03T error al realizar el delay
        }
        try{
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS));
            if(jsonArray2 != null && !jsonArray2.isEmpty()){
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
            }else {
                JSONArray jarray = new JSONArray();
                try{
                    JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS_ROL3));
                    JSONArray jarray3 = new JSONArray();
                    if(jsonArray2 != null){
                        for(int i = 0; i < jsonArray2.length(); i++){
                            String nombre = jsonArray2.getJSONObject(i).get("nombre").toString();
                            //CREATE HORARIOS MENUS
                            rest.POST(
                                    routes.getRoute(Routes.routesName.CREATE_HORARIO),
                                    "id", nombre+txtNuevoTmenu.getText(),
                                    "empresa", nombre,
                                    "menu", txtNuevoTmenu.getText(),
                                    "hInicioRes", "-------",
                                    "hFinRes", "-------",
                                    "hInicioEnt", "-------",
                                    "hFinEnt", "-------"
                            );
                            //Create disponibilidad por menus
                            rest.POST(
                                    routes.getRoute(Routes.routesName.CREATE_DISPO_MENU),
                                    "id", nombre+txtNuevoTmenu.getText(),
                                    "empresa", nombre,
                                    "menu", txtNuevoTmenu.getText(),
                                    "lunesref", "0",
                                    "martesref", "0",
                                    "miercolesref", "0",
                                    "juevesref", "0",
                                    "vienesref", "0",
                                    "sabadoref", "0",
                                    "domingoref", "0",
                                    "lunes", "0",
                                    "martes", "0",
                                    "miercoles", "0",
                                    "jueves", "0",
                                    "viernes", "0",
                                    "sabado", "0",
                                    "domingo", "0"
                            );
                            //CREACION MENU EMPRESA
                            JSONObject jObject = new JSONObject();
                            jObject.put("id",nombre+txtNuevoTmenu.getText());
                            jObject.put("empresa",nombre);
                            jObject.put("menu",txtNuevoTmenu.getText());
                            jObject.put("check",false);
                            jarray.put(jObject);
                        }
                        rest.POSTARRAY(routes.getRoute(Routes.routesName.CREATE_MENU_EMPRESA), jarray);
                    }else {
                        //listaTmenu.setPlaceholder(new Label("No se encontraron menus"));
                        //listaTmenu.setDisable(true);
                    }
                }catch(Exception e){
                    helper.showAlert("Ocurrió un error al consultar el listado de menus, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
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
        checkCol.setPrefWidth(23);
        nombreMenuCol.setPrefWidth(326);
        tableview.getColumns().addAll(checkCol, nombreMenuCol);
        listaRoles.add("Administrador contrato");
        listviewVacia="No Se Encontraron Restaurantes";
        if(UsuarioEntity.getRol()==1){
            listaRoles.add("Supervisor");
            listviewVacia="No Se Encontraron Restaurantes o Supervisores";
        }
        this.comboboxRol.setItems(listaRoles);
        ObservableList<String> listaHoras = FXCollections.observableArrayList("01","02","03","04","05","06","07","08","09","10","11","12");
        ObservableList<String> listaMinutos = FXCollections.observableArrayList("00","15","30","45");
        ObservableList<String> listaAmPm = FXCollections.observableArrayList("AM","PM");
        this.horaInicioReserva.setItems(listaHoras);
        this.horaFinReserva.setItems(listaHoras);
        this.horaInicioEntrega.setItems(listaHoras);
        this.horaFinEntrega.setItems(listaHoras);
        this.minutoInicioReserva.setItems(listaMinutos);
        this.minutoFinReserva.setItems(listaMinutos);
        this.minutoInicioEntrega.setItems(listaMinutos);
        this.minutoFinEntrega.setItems(listaMinutos);
        this.amInicioReserva.setItems(listaAmPm);
        this.amFinReserva.setItems(listaAmPm);
        this.amInicioEntrega.setItems(listaAmPm);
        this.amFinEntrega.setItems(listaAmPm);
        cargarListaUsers();
    }

    public void okSinPermisos() {
        paneSinPermisos.setVisible(false);
    }

    public void btnCerrarSesion(MouseEvent event) throws IOException {
        helper.show("logIn.fxml", parentPane);
        UsuarioEntity.destroy();
    }

    public void selectMenuMouseClicked(){
        paneAsignarMenu.setVisible(true);
        this.listaRestaurante.setDisable(true);
        this.txtNuevoRestaurante.setDisable(true);
        this.btnAgregar.setVisible(false);
        this.btnEditar.setVisible(false);
        this.btnEliminar.setVisible(false);
        this.btnSelectMenu.setDisable(true);
        try{
            listaMenus.clear();
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS_EMPRESA, listaRestaurante.getSelectionModel().getSelectedItem().toString()));
            if(jsonArray2 != null){
                //listaMenus.removeAll();
                for(int i = 0; i < jsonArray2.length(); i++){
                    listaMenus.add(new MenuModel((Boolean) jsonArray2.getJSONObject(i).get("check"), (String) jsonArray2.getJSONObject(i).get("menu")));
                }
            }else {
                //listaTmenu.setPlaceholder(new Label("No se encontraron menus"));
                //listaTmenu.setDisable(true);
            }
            nombreMenuCol.setCellValueFactory(
                    new PropertyValueFactory<MenuModel,String>("nombreMenu")
            );

            checkCol.setCellValueFactory(
                    new PropertyValueFactory<MenuModel,String>("remark")
            );

            tableview.setItems(listaMenus);
            /*
            checkCol.setCellValueFactory(
                    new PropertyValueFactory<MenuModel,String>("isSelected")
            );
            nombreMenuCol.setCellValueFactory(
                    new PropertyValueFactory<MenuModel,String>("nMenu")
            );
            tableview.setItems(listaMenus);*/
        }catch(Exception e){
            helper.showAlert("Ocurrió un error al consultar el listado de menus, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
    }

    public void cerrarPopupAsignarMenu(){
        paneAsignarMenu.setVisible(false);
        this.listaRestaurante.setDisable(false);
        this.txtNuevoRestaurante.setDisable(false);
        txtMostrarBotonAgregar();
        ListaRestauranteMouseClicked();
        this.btnSelectMenu.setDisable(false);
        this.tableview.setItems(null);
    }

    public void saveMenuChanges() {
        try{
            JSONArray jarray = new JSONArray();
            for(int i = 0; i < listaMenus.size(); i++){
                JSONObject jObject = new JSONObject();
                String menu = listaMenus.get(i).getNombreMenu();
                jObject.put("id",listaRestaurante.getSelectionModel().getSelectedItem().toString()+menu);
                jObject.put("empresa",listaRestaurante.getSelectionModel().getSelectedItem().toString());
                jObject.put("menu",menu);
                jObject.put("check",listaMenus.get(i).getRemark().isSelected());
                jarray.put(jObject);
            }
            String respuesta = rest.POSTARRAY(routes.getRoute(Routes.routesName.CREATE_MENU_EMPRESA), jarray);
            if(!respuesta.equals("sucess")){
                helper.showAlert("Ocurrió un error al asignar los menus, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            }else{
                paneAsignarMenuSuccess.setVisible(true);
            }

        }catch (Exception e){
            System.out.println("error momentaneo");
        }
    }

    public void cargarDiasMenu(){
        try{
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_DIAS, ListaRestaurant.getValue().toString()));
            if(jsonArray2 != null){
                checkLunes.setDisable(false);
                checkMartes.setDisable(false);
                checkMiercoles.setDisable(false);
                checkJueves.setDisable(false);
                checkViernes.setDisable(false);
                checkSabado.setDisable(false);
                checkDomingo.setDisable(false);
                cambioDispoImg.setDisable(false);
                cambioDispoTxt.setDisable(false);
                ListaMenuReservas.setDisable(false);

                checkLunes.setSelected((Boolean) jsonArray2.getJSONObject(0).get("lunes"));
                checkMartes.setSelected((Boolean) jsonArray2.getJSONObject(0).get("martes"));
                checkMiercoles.setSelected((Boolean) jsonArray2.getJSONObject(0).get("miercoles"));
                checkJueves.setSelected((Boolean) jsonArray2.getJSONObject(0).get("jueves"));
                checkViernes.setSelected((Boolean) jsonArray2.getJSONObject(0).get("viernes"));
                checkSabado.setSelected((Boolean) jsonArray2.getJSONObject(0).get("sabado"));
                checkDomingo.setSelected((Boolean) jsonArray2.getJSONObject(0).get("domingo"));

                try{
                    ObservableList<String> listatrue = FXCollections.observableArrayList();
                    JSONArray jsonArray3 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS_TRUE, ListaRestaurant.getValue().toString()));
                    if(jsonArray3 != null){
                        for(int i = 0; i < jsonArray3.length(); i++){
                            listatrue.add((String) jsonArray3.getJSONObject(i).get("menu"));
                            ListaMenuReservas.setItems(listatrue);
                        }
                    }else {
                        System.out.println("F");
                    }
                }catch (Exception e){

                }

            }else {
                //listaTmenu.setPlaceholder(new Label("No se encontraron menus"));
                //listaTmenu.setDisable(true);
            }
        }catch (Exception e){

        }
    }

    public void getHoras(){
        horaInicioReserva.setDisable(false);
        minutoInicioReserva.setDisable(false);
        amInicioReserva.setDisable(false);
        horaFinReserva.setDisable(false);
        minutoFinReserva.setDisable(false);
        amFinReserva.setDisable(false);
        horaInicioEntrega.setDisable(false);
        minutoInicioEntrega.setDisable(false);
        amInicioEntrega.setDisable(false);
        horaFinEntrega.setDisable(false);
        minutoFinEntrega.setDisable(false);
        amFinEntrega.setDisable(false);
        cambioHorarioImg.setDisable(false);
        cambioHorarioTxt.setDisable(false);
        try{
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_HORAS, ListaRestaurant.getValue().toString(), ListaMenuReservas.getValue().toString()));
            if(jsonArray2 != null) {
                horaInicioReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioRes").toString().substring(0,2));
                minutoInicioReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioRes").toString().substring(3,5));
                amInicioReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioRes").toString().substring(5));

                horaFinReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinRes").toString().substring(0,2));
                minutoFinReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinRes").toString().substring(3,5));
                amFinReserva.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinRes").toString().substring(5));

                horaInicioEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioEnt").toString().substring(0,2));
                minutoInicioEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioEnt").toString().substring(3,5));
                amInicioEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hInicioEnt").toString().substring(5));

                horaFinEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinEnt").toString().substring(0,2));
                minutoFinEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinEnt").toString().substring(3,5));
                amFinEntrega.getSelectionModel().select(jsonArray2.getJSONObject(0).get("hFinEnt").toString().substring(5));
            }else {
                System.out.println("oh my");
            }
        }catch (Exception e){

        }
    }

    public void cambiarDisponibilidad(){
        try {
            String respuesta = rest.POST(
                    routes.getRoute(Routes.routesName.CREATE_DIAS),
                    "id", ListaRestaurant.getValue().toString(),
                    "empresa", ListaRestaurant.getValue().toString(),
                    "lunes", String.valueOf(checkLunes.isSelected()),
                    "martes", String.valueOf(checkMartes.isSelected()),
                    "miercoles", String.valueOf(checkMiercoles.isSelected()),
                    "jueves", String.valueOf(checkJueves.isSelected()),
                    "viernes", String.valueOf(checkViernes.isSelected()),
                    "sabado", String.valueOf(checkSabado.isSelected()),
                    "domingo", String.valueOf(checkDomingo.isSelected())
            );

            if(!respuesta.equals("sucess")){
                helper.showAlert("Ocurrió un error al guardar la disponibilidad, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            }else{
                paneDispoSuccess.setVisible(true);
            }
        }catch (Exception e){
            helper.showAlert("Ocurrió un error al guardar la disponibilidad, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
    }

    public void cambiarHorarios(){
        String hInicioRes = horaInicioReserva.getValue().toString()+":"+minutoInicioReserva.getValue().toString()+amInicioReserva.getValue().toString();
        String hFinRes = horaFinReserva.getValue().toString()+":"+minutoFinReserva.getValue().toString()+amFinReserva.getValue().toString();
        String hInicioEnt = horaInicioEntrega.getValue().toString()+":"+minutoInicioEntrega.getValue().toString()+amInicioEntrega.getValue().toString();
        String hFinEnt = horaFinEntrega.getValue().toString()+":"+minutoFinEntrega.getValue().toString()+amFinEntrega.getValue().toString();

        if(hInicioRes.contains("-") || hFinRes.contains("-") || hInicioEnt.contains("-") || hFinEnt.contains("-")){
            helper.showAlert("No se han inicializado de forma correcta los horarios, por favor asigne un valor valido a cada uno de los horarios", Alert.AlertType.ERROR);
        }else {
            try {
                System.out.println("time; "+hInicioEnt);

                String respuesta = rest.POST(
                        routes.getRoute(Routes.routesName.CREATE_HORARIO),
                        "id", ListaRestaurant.getValue().toString()+ListaMenuReservas.getValue().toString(),
                        "empresa", ListaRestaurant.getValue().toString(),
                        "menu", ListaMenuReservas.getValue().toString(),
                        "hInicioRes", hInicioRes,
                        "hFinRes", hFinRes,
                        "hInicioEnt", hInicioEnt,
                        "hFinEnt", hFinEnt
                );

                if(!respuesta.equals("sucess")){
                    helper.showAlert("Ocurrió un error al guardar los horarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                }else{
                    paneDispoSuccess.setVisible(true);
                }
            }catch (Exception e){
                helper.showAlert("Ocurrió un error al guardar los horarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            }
        }
    }

    public void cerrarPopupDispoSuccess() {
        paneDispoSuccess.setVisible(false);
    }

    public void cerrarPopupAsignarMenuSuccess() {
        paneAsignarMenuSuccess.setVisible(false);
    }
}

