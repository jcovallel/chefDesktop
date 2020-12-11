import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.passay.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UsuarioNormal extends Usuario implements Initializable{
    @FXML
    private TextField lunes_val,martes_val,miercoles_val,jueves_val,viernes_val,sabado_val,domingo_val;

    @FXML
    private ComboBox comboBoxTmenu;

    @FXML
    private AnchorPane paneDispoSuccess;

    @FXML
    private Label archivocargado, labelUsuario, cambiarDispoLabel;

    @FXML
    private ImageView cambiarDispoImage;

    @FXML
    private Tab reservaTab;

    @FXML
    private AnchorPane draggable, parentPane;

    public List <String> imagepath = new ArrayList<String>();
    private String ip = "127.0.0.1";
    private String puerto = "8080";
    private String urlRaiz = "http://" + ip + ":" + puerto;

    public void tabReservas(){
        lunes_val.setDisable(true);
        martes_val.setDisable(true);
        miercoles_val.setDisable(true);
        jueves_val.setDisable(true);
        viernes_val.setDisable(true);
        sabado_val.setDisable(true);
        domingo_val.setDisable(true);
        cambiarDispoLabel.setDisable(true);
        cambiarDispoImage.setDisable(true);
        if(reservaTab.isSelected()) {
            try{
                ObservableList<String> listatrue = FXCollections.observableArrayList();
                JSONArray jsonArray3 = rest.GET(routes.getRoute(Routes.routesName.GET_MENUS_TRUE, UsuarioEntity.getNombre()));
                if(jsonArray3 != null){
                    for(int i = 0; i < jsonArray3.length(); i++){
                        listatrue.add((String) jsonArray3.getJSONObject(i).get("menu"));
                        comboBoxTmenu.setItems(listatrue);
                    }
                }else {
                    System.out.println("F");
                }
            }catch (Exception e){

            }
        }else{
            this.comboBoxTmenu.getSelectionModel().clearSelection();
            lunes_val.setText("");
            martes_val.setText("");
            miercoles_val.setText("");
            jueves_val.setText("");
            viernes_val.setText("");
            sabado_val.setText("");
            domingo_val.setText("");
        }
    }

    public void ButtonUploadImageAction(MouseEvent event){
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(imageFilter);
        List <File> selectedFile = fc.showOpenMultipleDialog(null);
        if (!selectedFile.isEmpty()){
            for(int i=0;i<selectedFile.size();i++){
                if(i==0){
                    archivocargado.setText(selectedFile.get(0).getName());
                }
                if(i==1){
                    archivocargado.setText(archivocargado.getText()+" y "+(selectedFile.size()-1)+" archivo(s) más");
                }
                imagepath.add(selectedFile.get(i).getAbsolutePath());
            }
        }else {
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }
    }

    public void dragImagen(){
        final List<String> validExtensions = Arrays.asList("jpg", "png", "jpeg");
        draggable.setOnDragOver(event -> {
            // On drag over if the DragBoard has files
            if (event.getGestureSource() != draggable && event.getDragboard().hasFiles()) {
                // All files on the dragboard must have an accepted extension
                if (!validExtensions.containsAll(
                        event.getDragboard().getFiles().stream()
                                .map(file -> getExtension(file.getName()))
                                .collect(Collectors.toList()))) {

                    event.consume();
                    return;
                }

                // Allow for both copying and moving
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        draggable.setOnDragDropped(event -> {
            AtomicInteger index = new AtomicInteger();
            boolean success = false;
            if (event.getGestureSource() != draggable && event.getDragboard().hasFiles()) {
                // Print files
                event.getDragboard().getFiles().forEach(file -> {
                    if(index.get()==0){
                        archivocargado.setText(file.getName());
                    }
                    if(index.get()==1){
                        archivocargado.setText(archivocargado.getText()+" y "+(event.getDragboard().getFiles().size()-1)+" archivo(s) más");
                    }
                    imagepath.add(file.getAbsolutePath());
                    index.getAndIncrement();
                });
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void cuentaAceptar(MouseEvent event){
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

    public void cerrarPopupCuenta(MouseEvent event){
        panelConfirmarCuenta.setVisible(false);
    }

    public void ButtonUploadAction(MouseEvent event){
        if(imagepath.isEmpty()){
            helper.showAlert("Aún no se ha seleccionado ninguna imagen", Alert.AlertType.INFORMATION);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Do post (read, from link below)
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpPost uploadFile = new HttpPost(urlRaiz + routes.getRoute(Routes.routesName.UPLOAD_IMAGE, UsuarioEntity.getNombre()));

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

                    // This attaches the file to the POST:
                    for(int i=0; i<imagepath.size();i++){
                        File f = new File(imagepath.get(i));
                        try {
                            builder.addBinaryBody(
                                    "imageFile",
                                    new FileInputStream(f),
                                    ContentType.APPLICATION_OCTET_STREAM,
                                    f.getName()
                            );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    HttpEntity multipart = builder.build();
                    uploadFile.setEntity(multipart);
                    CloseableHttpResponse response = null;
                    try {
                        response = httpClient.execute(uploadFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    HttpEntity responseEntity = response.getEntity();

                    //Update of fragment from that article about POST in Java:
                    if (responseEntity != null) {
                        InputStream instream = null;
                        try {
                            instream = responseEntity.getContent();
                            if(EntityUtils.toString(responseEntity).equalsIgnoreCase("exitosa")){
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        imagepath.clear();
                                        archivocargado.setText("No ha seleccionado ninguna imagen");
                                        helper.showAlert("¡Imagen cargada satisfactoriamente!", Alert.AlertType.CONFIRMATION);
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            //If you need to update of UI here, use Platform.runLater(Runnable);
                        } finally {
                            try {
                                instream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public void modifyDispo(MouseEvent event) throws IOException {
        try{
            // creacion disponibilidad menu
            String respuesta = rest.POST(
                    routes.getRoute(Routes.routesName.CREATE_DISPO_MENU),
                    "id", UsuarioEntity.getNombre()+comboBoxTmenu.getValue().toString(),
                    "empresa", UsuarioEntity.getNombre(),
                    "menu", comboBoxTmenu.getValue().toString(),
                    "lunesref", lunes_val.getText(),
                    "martesref", martes_val.getText(),
                    "miercolesref", miercoles_val.getText(),
                    "juevesref", jueves_val.getText(),
                    "viernesref", viernes_val.getText(),
                    "sabadoref", sabado_val.getText(),
                    "domingoref", domingo_val.getText(),
                    "lunes", lunes_val.getText(),
                    "martes", martes_val.getText(),
                    "miercoles", miercoles_val.getText(),
                    "jueves", jueves_val.getText(),
                    "viernes", viernes_val.getText(),
                    "sabado", sabado_val.getText(),
                    "domingo", domingo_val.getText()
            );

            if(!respuesta.equals("sucess")){
                helper.showAlert("Ocurrió un error al al guardar los datos, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            }else {
                paneDispoSuccess.setVisible(true);
            }
        }catch (Exception e){
            helper.showAlert("Ocurrió un error al al guardar los datos, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
        }
    }

    public void getexcel(MouseEvent event) throws ClientProtocolException, IOException{
        rest.GETExcel(
                routes.getRoute(Routes.routesName.GET_EXCEL_DISPONIBILIDAD, UsuarioEntity.getNombre()),
                "Reservas");

    }

    public void getDisponibilidadMenus(){
        cambiarDispoLabel.setDisable(false);
        cambiarDispoImage.setDisable(false);
        try{
            JSONArray jsonArray2 = rest.GET(routes.getRoute(Routes.routesName.GET_DISPO_MENUREF, UsuarioEntity.getNombre(), comboBoxTmenu.getValue().toString()));
            JSONArray jsonArray3 = rest.GET(routes.getRoute(Routes.routesName.GET_DIAS, UsuarioEntity.getNombre()));
            if(jsonArray2 != null) {
                if((Boolean) jsonArray3.getJSONObject(0).get("lunes")){
                    lunes_val.setDisable(false);
                    lunes_val.setText(jsonArray2.getJSONObject(0).get("lunesref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("martes")){
                    martes_val.setDisable(false);
                    martes_val.setText(jsonArray2.getJSONObject(0).get("martesref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("miercoles")){
                    miercoles_val.setDisable(false);
                    miercoles_val.setText(jsonArray2.getJSONObject(0).get("miercolesref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("jueves")){
                    jueves_val.setDisable(false);
                    jueves_val.setText(jsonArray2.getJSONObject(0).get("juevesref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("viernes")){
                    viernes_val.setDisable(false);
                    viernes_val.setText(jsonArray2.getJSONObject(0).get("viernesref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("sabado")){
                    sabado_val.setDisable(false);
                    sabado_val.setText(jsonArray2.getJSONObject(0).get("sabadoref").toString());
                }
                if((Boolean) jsonArray3.getJSONObject(0).get("domingo")){
                    domingo_val.setDisable(false);
                    domingo_val.setText(jsonArray2.getJSONObject(0).get("domingoref").toString());
                }
            }else {
                System.out.println("oh my");
            }
        }catch (Exception e){

        }
    }

    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelUsuario.setText(UsuarioEntity.getNombre());
        try {
            cargarTabla();
        } catch (IOException e) {
            helper.showAlert("Ocurrió un error inesperado", Alert.AlertType.ERROR);
        }
    }

    public void recargarComentarios(MouseEvent event) throws IOException {
        cargarTabla();
    }

    public void btnCerrarSesion(MouseEvent event) throws IOException {
        helper.show("logIn.fxml", parentPane);
        UsuarioEntity.destroy();
    }

    public void cerrarPopupDispoSuccess() {
        paneDispoSuccess.setVisible(false);
    }
}
