import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Usuario extends Application{

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
        rest.GETExcel(
                routes.getRoute(
                        Routes.routesName.GET_EXCEL_COMENTARIOS,
                        UsuarioEntity.getNombre()
                ),
                "Reporte-comentarios"
        );
    }

    @FXML
    protected void tabComentarios() throws IOException {

    }

    public void tabCuenta() throws IOException {
        JSONArray jsonArray = rest.GET(
                routes.getRoute(
                        Routes.routesName.GET_EMAIL,
                        UsuarioEntity.getNombre()
                ),
                true
        );

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
        JSONArray jsonArray = rest.GET(
                routes.getRoute(
                        Routes.routesName.GET_PASS,
                        UsuarioEntity.getNombre(),
                        helper.hash(txtPassActual.getText())
                )
        );
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
                        rest.PUT(
                                routes.getRoute(
                                        Routes.routesName.MODIFY_USUARIO,
                                        UsuarioEntity.getNombre(),
                                        nuevoPass,
                                        nuevoCorreo
                                )
                        );
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
                if(jsonArray != null){
                    if(jsonArray.getJSONObject(0).get("acceso").toString().contains("true")){
                        rest.PUT(
                                routes.getRoute(
                                        Routes.routesName.MODIFY_USUARIO,
                                        UsuarioEntity.getNombre(),
                                        nuevoPass,
                                        nuevoCorreo
                                )
                        );
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

    protected void cargarTabla() throws IOException {
        if(!tableCalificacion.getItems().isEmpty()){
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
        }

        JSONArray jsonArray = rest.GET(
                routes.getRoute(
                        Routes.routesName.GET_REVIEWS_USUARIOS,
                        UsuarioEntity.getNombre()
                )
        );
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

        if(!listaCalificaciones.isEmpty()) {
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
    }
}
