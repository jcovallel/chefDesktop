import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
                    if(UsuarioEntity.getUsuario("").getNombre().equals("Administrador")){
                        helper.show("usuarioAdmin.fxml", parentPane);
                    }
                    else{
                        helper.show("usuarioNormal.fxml", parentPane);
                    }
                }
                catch (Exception e){
                    labelError.setText("Error al cambiar la contraseña");
                    paneError.setVisible(true);
                    e.printStackTrace();
                }
            }
        }
    }

    public void okError(MouseEvent event) {
        paneError.setVisible(false);
    }
}
