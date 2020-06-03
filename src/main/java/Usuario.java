import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

import java.io.IOException;


public class Usuario {

    @FXML
    protected TextField txtCorreo;

    @FXML
    protected PasswordField txtNuevoPass, txtNuevoPassAgain, txtPassActual;

    @FXML
    protected TableView<Calificacion> tableCalificacion;

    @FXML
    protected AnchorPane panelConfirmarCuenta, paneCuentaError;

    @FXML
    Label labelCuentaError;

    protected static Boolean entrando = true;
    protected Helper helper = new Helper();
    REST rest = new REST();

    protected ObservableList<Calificacion> listaCalificaciones = FXCollections.observableArrayList();

    @FXML
    protected void txtNuevoPassActionPerformed(KeyEvent event){
        if(this.txtNuevoPass.getText().length() > 0){
            this.txtNuevoPassAgain.setVisible(true);
        }
        else{
            this.txtNuevoPassAgain.setVisible(false);
        }
    }

    @FXML
    protected void descargarReporteCalificaciones(MouseEvent event) throws IOException {
        rest.GETExcel("/chef/download_excel_comen/" + UsuarioEntity.getNombre().replaceAll(" ", "%20") ,"Reporte-Comentarios");
    }

    @FXML
    protected void tabComentarios() throws IOException {
        if(entrando){
            String path = "/chef/user/review/" + UsuarioEntity.getNombre().replaceAll(" ", "%20");
            JSONArray jsonArray = rest.GET(path);
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

                helper.setTablaCalificacion(
                        tableCalificacion,
                        listaCalificaciones,
                        Double.parseDouble("80"),
                        Double.parseDouble("300"),
                        Double.parseDouble("150"),
                        Double.parseDouble("150"),
                        Double.parseDouble("150")
                );
            }
            entrando=false;
        }else{
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
            entrando=true;
        }
    }

    public void tabCuenta() throws IOException {
        // chef/getmail/{empresa}
        String path = "/chef/getmail/" + UsuarioEntity.getNombre().replaceAll(" ", "%20");
        JSONArray jsonArray = rest.GET(path, true);
        if(jsonArray != null){
            txtCorreo.setText(jsonArray.getJSONObject(0).getString("correo"));
        }
        txtNuevoPass.setText("");
        txtNuevoPassAgain.setText("");
        txtPassActual.setText("");
    }

    public void cerrarPopupCuenta(MouseEvent event) {
        panelConfirmarCuenta.setVisible(false);
    }

    public void okAceptarEditarCuenta(MouseEvent event) throws IOException {
        String nuevoCorreo;
        String nuevoPass = "NULL";
        String path = "/chef/getpass/" + UsuarioEntity.getNombre().replaceAll(" ", "%20") + "/" + helper.hash(txtPassActual.getText());
        JSONArray jsonArray = rest.GET(path);
        System.out.println(jsonArray);
        if(txtCorreo.getText().length() <= 0){
            nuevoCorreo = "NULL";
        }
        else{
            nuevoCorreo = txtCorreo.getText();
        }

        if(txtNuevoPass.getText().length() <= 0 && txtNuevoPassAgain.getText().length() <= 0){
            nuevoPass = "NULL";
            if(!nuevoCorreo.equals("NULL")){
                if(jsonArray != null){
                    if(jsonArray.getJSONObject(0).get("acceso").toString().contains("true")){
                        path = "/chef/modifydatausers/" + UsuarioEntity.getNombre() + "/" + nuevoPass + "/" + nuevoCorreo;
                        rest.PUT(path);
                        panelConfirmarCuenta.setVisible(false);
                        labelCuentaError.setText("La información se actualizó satisfactoriamente");
                        paneCuentaError.setVisible(true);
                    }
                    else{
                        labelCuentaError.setText("Contraseña incorrecta");
                        paneCuentaError.setVisible(true);
                    }
                }
                else{
                    labelCuentaError.setText("Ocurrió un error. Intente más tarde");
                    paneCuentaError.setVisible(true);
                }
            }
            else{
                labelCuentaError.setText("Tiene que haber por lo menos un valor para modificar");
                paneCuentaError.setVisible(true);
            }
        }
        else{
            if(txtNuevoPass.getText().equals(txtNuevoPassAgain.getText())){

                nuevoPass = helper.hash(txtNuevoPass.getText());
                System.out.println(jsonArray);
                if(jsonArray != null){
                    if(jsonArray.getJSONObject(0).get("acceso").toString().contains("true")){
                        path = "/chef/modifydatausers/" + UsuarioEntity.getNombre().replaceAll(" ", "%20") + "/" + nuevoPass + "/" + nuevoCorreo;
                        rest.PUT(path);
                        panelConfirmarCuenta.setVisible(false);
                        labelCuentaError.setText("La información se actualizó satisfactoriamente");
                        paneCuentaError.setVisible(true);
                    }
                    else{
                        labelCuentaError.setText("Contraseña incorrecta");
                        paneCuentaError.setVisible(true);
                    }
                }
                else{
                    labelCuentaError.setText("Ocurrió un error. Intente más tarde");
                    paneCuentaError.setVisible(true);
                }
            }
            else{
                labelCuentaError.setText("Las contraseñas no coinciden");
                paneCuentaError.setVisible(true);
            }
        }
        panelConfirmarCuenta.setVisible(false);
    }

    public void cerrarPaneCuentaError(MouseEvent event) {
        paneCuentaError.setVisible(false);
    }
}
