import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.passay.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CambioPass extends Application{

    @FXML
    private AnchorPane paneError, parentPane;

    @FXML
    private PasswordField txtPass, txtPass1;

    @FXML
    private Label labelError;

    List<Rule> rules = new ArrayList();

    public void btnAceptarActionPerformed(javafx.event.ActionEvent actionEvent) throws IOException {
        rules.add(new LengthRule(8));
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 2));
        rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
        PasswordValidator validator = new PasswordValidator(rules);
        if(txtPass.getText().length() <= 0 || txtPass1.getText().length() <= 0){
            labelError.setText("Los dos campos deben ser completados");
            paneError.setVisible(true);
        }
        else if(!txtPass.getText().equals(txtPass1.getText())){
            labelError.setText("Las contraseñas no coinciden");
            paneError.setVisible(true);
        }
        else{
            PasswordData password = new PasswordData(txtPass.getText());
            RuleResult result = validator.validate(password);
            if(!result.isValid()){
                labelError.setText("La contraseña debe serguir los estandares impuestos");
                paneError.setVisible(true);
            }
            else{

                try{
                    rest.PUT(
                            routes.getRoute(
                                    Routes.routesName.MODIFY_USUARIO,
                                    UsuarioEntity.getNombre(),
                                    helper.hash(txtPass.getText()), "NULL"
                            )
                    );
                    JSONArray jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_ROL, UsuarioEntity.getUsuario("", 0).getNombre()));
                    System.out.println(jsonArray.getJSONObject(0));
                    if(jsonArray.getJSONObject(0).get("response").toString().equals("2") || jsonArray.getJSONObject(0).get("response").toString().equals("1")){
                        helper.show("usuarioAdmin.fxml", parentPane);
                    }
                    else if(jsonArray.getJSONObject(0).get("response").toString().equals("3")){
                        helper.show("usuarioNormal.fxml", parentPane);
                    }
                    else{
                        labelError.setText("ocurrió un error inesperado");
                        paneError.setVisible(true);
                    }
                }
                catch (Exception e){
                    labelError.setText("Error al cambiar la contraseña");
                    paneError.setVisible(true);
                }
            }
        }
    }

    public void okError(MouseEvent event) {
        paneError.setVisible(false);
    }
}
