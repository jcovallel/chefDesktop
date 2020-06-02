import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
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

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UsuarioNormal extends Usuario implements Initializable{
    @FXML
    private TextField lunes_val,martes_val,miercoles_val,jueves_val,viernes_val;

    @FXML
    private Label archivocargado, labelUsuario;

    @FXML
    private AnchorPane draggable;

    public String imagepath="vacio397";
    private String ip = "35.188.127.60";
    private String puerto = "8080";
    private String urlRaiz = "http://" + ip + ":" + puerto;

    Helper helper = new Helper();

    public void ButtonUploadImageAction(MouseEvent event){
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(imageFilter);
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null){
            archivocargado.setText(selectedFile.getName());
            imagepath=selectedFile.getAbsolutePath();
        }else {
            System.out.println("file is not valid");
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
            boolean success = false;
            if (event.getGestureSource() != draggable && event.getDragboard().hasFiles()) {
                // Print files
                event.getDragboard().getFiles().forEach(file -> {
                    archivocargado.setText(file.getName());
                    imagepath=file.getAbsolutePath();
                });
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void cuentaAceptar(MouseEvent event){
        panelConfirmarCuenta.setVisible(true);
    }

    public void cerrarPopupCuenta(MouseEvent event){
        panelConfirmarCuenta.setVisible(false);
    }

    public void ButtonUploadAction(MouseEvent event){
        if(imagepath.equalsIgnoreCase("vacio397")){
            helper.showAlert("Aún no se ha seleccionado ninguna imagen", Alert.AlertType.INFORMATION);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Do post (read, from link below)
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    HttpPost uploadFile = new HttpPost(urlRaiz + "/chef/uploadmenu");

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

                    // This attaches the file to the POST:
                    File f = new File(imagepath);
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
                                        imagepath="vacio397";
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

        String path = "/chef/disponibilidad/" + UsuarioEntity.getNombre();
        JSONArray jsonArray = rest.PUT(path,
                "empresaid", UsuarioEntity.getNombre(),
                "empresa", UsuarioEntity.getNombre(),
                "Lunes", lunes_val.getText(),
                "Martes", martes_val.getText(),
                "Miercoles", miercoles_val.getText(),
                "Jueves", jueves_val.getText(),
                "Viernes", viernes_val.getText()
                );
        System.out.println(jsonArray);
        if(jsonArray != null){
            helper.showAlert("Disponibilidad actualizada satisfactoriamente", Alert.AlertType.CONFIRMATION);
        }
        else{
            helper.showAlert("Ocurrió un error", Alert.AlertType.ERROR);
        }
    }

    public void getexcel(MouseEvent event) throws ClientProtocolException, IOException{
        String getEndpoint = urlRaiz + "/chef/download_excel";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpget = new HttpGet(getEndpoint);

        HttpResponse response = httpclient.execute(httpget);

        //BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        //Throw runtime exception if status code isn't 200
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedInputStream bis = new BufferedInputStream(entity.getContent());
            String filePath = "Reservas.xlsx";
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            int inByte;
            while((inByte = bis.read()) != -1) bos.write(inByte);
            bis.close();
            bos.close();

            helper.showAlert("Archivo descargado exitosamente", Alert.AlertType.CONFIRMATION);
        }

        //Create the StringBuffer object and store the response into it.
        /*StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            System.out.println("Response : \n"+result.append(line));
        }*/
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
    }
}
