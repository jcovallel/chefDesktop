import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RestorePass extends Application implements Initializable {

    @FXML
    private TextField txtCorreo;

    @FXML
    private AnchorPane paneCorreoEnviado, paneRestorePass, paneError, parentPane;

    @FXML
    private ComboBox<String> comboboxUsuario;

    ObservableList<String> listaLugares = FXCollections.observableArrayList();

    @FXML
    public void btnCancelarRestorePassActionPerformed() throws IOException {
        helper.show("logIn.fxml", parentPane);
    }

    public void closePopupRestorePass(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        this.paneCorreoEnviado.setVisible(false);
        helper.show("logIn.fxml", parentPane);
    }

    public void btnAceptarRestorePassActionPerformed(ActionEvent actionEvent) throws IOException {
        if(comboboxUsuario.getValue().length() > 0 && txtCorreo.getText().length() > 0){
            try {
                JSONArray jsonArray = rest.GET(
                        routes.getRoute(
                                Routes.routesName.SEND_EMAIL,
                                comboboxUsuario.getValue(),
                                txtCorreo.getText(), "true"
                        )
                );
                if(jsonArray != null){
                    if(jsonArray.getJSONObject(0).toString().contains("true")){
                        paneCorreoEnviado.setVisible(true);
                    }
                    else{
                        paneError.setVisible(true);
                    }
                }
                else{
                    paneError.setVisible(true);
                }
            }
            catch(Exception e){
                paneError.setVisible(true);
            }
        }
        else{
            paneError.setVisible(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JSONArray jsonArray;
        try{
            jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_USUARIOS));
            if(jsonArray != null){
                for(int i = 0; i < jsonArray.length(); i++){
                    listaLugares.add((String) jsonArray.getJSONObject(i).get("nombre"));
                }
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        listaLugares.sort((Object c1, Object c2)->{
            return c1.toString().compareTo((String) c2);
        });
        this.comboboxUsuario.setItems(listaLugares);
    }

    public void closePopupError(javafx.scene.input.MouseEvent event) {
        this.paneError.setVisible(false);
    }
}
