import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class LogIn extends Application implements Initializable{

    @FXML
    private AnchorPane parentPane, paneError;

    @FXML
    private PasswordField txtPass;

    @FXML
    private ComboBox<String> comboboxUsuario;

    @FXML
    Label labelError;

    ObservableList<String> listaLugares = FXCollections.observableArrayList();

    @FXML
    public void contrasenaOlvidadaActionPerformed() throws IOException {
        helper.show("restorePass.fxml", parentPane);
    }

    @FXML
    public void onEnterKey(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            btnAceptarActionPerformed();
        }
    }


    @FXML
    public void btnAceptarActionPerformed() throws IOException {
        JSONArray jsonArray;
        try{
            if(comboboxUsuario.getValue().length() > 0 && txtPass.getText().length() > 0){
                jsonArray = rest.GET(
                        routes.getRoute(
                                Routes.routesName.GET_PASS,
                                comboboxUsuario.getValue(),
                                helper.hash(txtPass.getText())
                        )
                );

                if(jsonArray != null ){
                    if(jsonArray.getJSONObject(0).get("acceso").toString().equals("true")){
                        Integer rol;
                        jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_ROL, comboboxUsuario.getValue()));
                        rol = (Integer) jsonArray.getJSONObject(0).get("response");
                        UsuarioEntity.getUsuario(comboboxUsuario.getValue(), rol);

                        if(helper.hash(txtPass.getText()).equals(helper.hash(helper.defaultPass))){
                            helper.show("cambioPass.fxml", parentPane);
                        }
                        else{
                            if(jsonArray.getJSONObject(0).get("response").toString().equals("2") || jsonArray.getJSONObject(0).get("response").toString().equals("1")){
                                helper.show("usuarioAdmin.fxml", parentPane);
                            }
                            else if(jsonArray.getJSONObject(0).get("response").toString().equals("3")){
                                helper.show("usuarioNormal.fxml", parentPane);
                            }
                            else{
                                labelError.setText("Ocurrió un error inesperado ERR02"); //ERROR 02 Error al determinar el rol del usuario actual
                                paneError.setVisible(true);
                            }
                        }
                    }
                    else{

                        labelError.setText("Usuario o contraseña incorrectos");
                        paneError.setVisible(true);
                        txtPass.setText("");
                    }

                }
                else{
                    labelError.setText("Ocurrió un error inesperado ERR01");//ERROR 01 Error al verificar la contraseña
                    paneError.setVisible(true);
                }
            }
            else{
                labelError.setText("Debe ingresar una contraseña");
                paneError.setVisible(true);
            }

        }
        catch(RuntimeException re){
            labelError.setText("Debe seleccionar un usuario");
            paneError.setVisible(true);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JSONArray jsonArray;
        try{
            jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS));
            if(jsonArray.length() != 0){
                for(int i = 0; i < jsonArray.length(); i++){
                    listaLugares.add((String) jsonArray.getJSONObject(i).get("nombre"));
                }
            }else{
                helper.showAlert("ERROR: No se encontraron usuarios, comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
                System.exit(0);
            }
        }
        catch(IOException ioe){
            helper.showAlert("Ocurrió un error al consultar el listado de usuarios, verifique su conexión a internet. Si el error persiste comuníquese con el administrador del sistema", Alert.AlertType.ERROR);
            System.exit(0);
        }
        listaLugares.sort((Object c1, Object c2) -> c1.toString().compareTo((String) c2) );
        this.comboboxUsuario.setItems(listaLugares);
    }

    public void closePopupError() {
        paneError.setVisible(false);
    }

}
