import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    private TextField lunes_val,martes_val,miercoles_val,jueves_val,viernes_val;

    @FXML
    private PasswordField txtNuevoPass, txtNuevoPassAgain;

    @FXML
    private Label archivocargado;

    @FXML
    private AnchorPane draggable, panelConfirmarCuenta;

    @FXML
    private ListView listastar,listacoment;

    public String imagepath="vacio397";
    public static Boolean entrando=true;

    private static final String ip = "35.239.78.54";
    private static final String puerto = "8080";
    private static String urlRaiz = "http://" + ip + ":" + puerto;

    public void testtab() throws ClientProtocolException, IOException{
        if(entrando){
            String getEndpoint = urlRaiz + "/chef/review/";

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
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                JSONArray myarr = new JSONArray(result);
                for (int i=0; i < myarr.length(); i++) {
                    listastar.getItems().add(myarr.getJSONObject(i).get("estrellas"));
                    listacoment.getItems().add(myarr.getJSONObject(i).get("comentario"));
                }
                instream.close();
            }
            entrando=false;
        }else{
            listastar.getItems().clear();
            listacoment.getItems().clear();
            entrando=true;
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

    public void Button1Action(ActionEvent event){
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

    public void test(){
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

    public void txtNuevoPassActionPerformed(KeyEvent event){
        if(this.txtNuevoPass.getText().length() > 0){
            this.txtNuevoPassAgain.setVisible(true);
        }
        else{
            this.txtNuevoPassAgain.setVisible(false);
        }
    }

    public void cuentaAceptar(MouseEvent event){
        this.panelConfirmarCuenta.setVisible(true);
    }

    public void cerrarPopup(MouseEvent event){
        this.panelConfirmarCuenta.setVisible(false);
    }

    //Comparar si los dos pass nuevos son iguales
    private Boolean compararPass(String pass1, String pass2){
        return pass1.equals(pass2);
    }

    public void ButtonUploadAction(ActionEvent event){
        if(imagepath.equalsIgnoreCase("vacio397")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Aun no ha seleccionado ninguna imagen");
            alert.showAndWait();
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
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setHeaderText(null);
                                        alert.setContentText("Imagen subida exitosamente!");
                                        alert.showAndWait();
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

    public void modifyDispo(ActionEvent event) throws ClientProtocolException, IOException {
        String putEndpoint = urlRaiz + "/chef/disponibilidad/";

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPut httpPut = new HttpPut(putEndpoint);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");

        String inputJson = "{\n" + "  \"id\": 0";
        if(!lunes_val.getText().isEmpty()){
            inputJson += ",\n  \"Lunes\": "+lunes_val.getText();
        }
        if(!martes_val.getText().isEmpty()){
            inputJson += ",\n  \"Martes\": "+martes_val.getText();
        }
        if(!miercoles_val.getText().isEmpty()){
            inputJson += ",\n  \"Miercoles\": "+miercoles_val.getText();
        }
        if(!jueves_val.getText().isEmpty()){
            inputJson += ",\n  \"Jueves\": "+jueves_val.getText();
        }
        if(!viernes_val.getText().isEmpty()){
            inputJson += ",\n  \"Viernes\": "+viernes_val.getText();
        }
        inputJson += "}";

        StringEntity stringEntity = new StringEntity(inputJson);
        httpPut.setEntity(stringEntity);

        HttpResponse response = httpclient.execute(httpPut);

        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        //Throw runtime exception if status code isn't 200
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }else{
            if(!(lunes_val.getText().isEmpty() && martes_val.getText().isEmpty() && miercoles_val.getText().isEmpty() && jueves_val.getText().isEmpty() && viernes_val.getText().isEmpty())){
                lunes_val.setText("");
                martes_val.setText("");
                miercoles_val.setText("");
                jueves_val.setText("");
                viernes_val.setText("");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Valores cargados correctamente!");
                alert.showAndWait();
            }
        }

        //Create the StringBuffer object and store the response into it.
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            System.out.println("Response : \n" + result.append(line));
        }
    }

    public void getexcel(ActionEvent event) throws ClientProtocolException, IOException{
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

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText(null);
            alert.setContentText("Archivo descargado exitosamente!");
            alert.showAndWait();
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
}
