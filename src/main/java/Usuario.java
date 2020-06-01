import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

import java.io.IOException;


public class Usuario {

    @FXML
    protected PasswordField txtNuevoPass, txtNuevoPassAgain;

    @FXML
    protected TableView<Calificacion> tableCalificacion;

    @FXML
    protected AnchorPane panelConfirmarCuenta;

    protected static Boolean entrando = true;
    protected Helper helper = new Helper();
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
    protected void tabComentarios() throws IOException {
        if(entrando){
            String path = "/chef/review/";
            JSONArray jsonArray = helper.GET(path);

            if (jsonArray != null) {
                for (int i=0; i < jsonArray.length(); i++) {
                    listaCalificaciones.add(
                            new Calificacion(
                                    (Integer) jsonArray.getJSONObject(i).get("estrellas"),
                                    (String) jsonArray.getJSONObject(i).get("comentario")
                            )
                    );
                }

                helper.setTablaCalificacion(tableCalificacion, "comentario",
                        listaCalificaciones, Double.parseDouble("80"), Double.parseDouble("465")
                );
            }
            entrando=false;
        }else{
            tableCalificacion.getItems().clear();
            tableCalificacion.getColumns().clear();
            entrando=true;
        }
    }
}
