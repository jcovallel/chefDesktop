import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.passay.*;

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

    List<Rule> rules = new ArrayList<>();

    public void btnAceptarActionPerformed() {
        rules.add(new LengthRule(8, 28));
        rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 2));
        rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
        PasswordValidator validator = new PasswordValidator(rules);
        if(txtPass.getText().length() <= 0 || txtPass1.getText().length() <= 0){
            labelError.setText("Debe ingresar los campos solicitados");
            paneError.setVisible(true);
        }
        else if(!txtPass.getText().equals(txtPass1.getText())){
            labelError.setText("Las contraseñas no coinciden");
            paneError.setVisible(true);
            txtPass.setText("");
            txtPass1.setText("");
        }
        else{
            PasswordData password = new PasswordData(txtPass.getText());
            RuleResult result = validator.validate(password);
            if(!result.isValid()){
                String error = validator.getMessages(result).get(0);
                String errorES = "";
                if(error.startsWith("Password must be 8")){
                    errorES = "La contraseña debe tener al menos 8 caracteres de longitud.";
                }else{
                    if(error.endsWith("special characters.")){
                        errorES = "La contraseña debe tener al menos 1 carácter especial.";
                    }else{
                        if(error.endsWith("uppercase characters.")){
                            errorES = "La contraseña debe tener al menos 1 carácter en mayúscula.";
                        }else{
                            if(error.endsWith("lowercase characters.")){
                                errorES = "La contraseña debe tener al menos 1 carácter en minúscula.";
                            }else{
                                if(error.endsWith("digit characters.")){
                                    errorES = "La contraseña debe tener al menos 2 digitos.";
                                }else{
                                    if(error.startsWith("Password must be no")){
                                        errorES = "La contraseña no debe tener más de 28 caracteres de longitud.";
                                    }
                                }
                            }
                        }
                    }
                }
                labelError.setText(errorES);
                labelError.setPrefHeight(55);
                paneError.setVisible(true);
            }
            else{

                try{
                    rest.POST(
                            routes.getRoute(Routes.routesName.MODIFY_USUARIO),
                            "id", UsuarioEntity.getNombre(),
                            "password", helper.hash(txtPass.getText()),
                            "correo", "NULL"
                    );
                    JSONArray jsonArray = rest.GET(routes.getRoute(Routes.routesName.GET_ROL, UsuarioEntity.getUsuario("", 0).getNombre()));
                    if(jsonArray.getJSONObject(0).get("response").toString().equals("2") || jsonArray.getJSONObject(0).get("response").toString().equals("1")){
                        helper.show("usuarioAdmin.fxml", parentPane);
                    }
                    else if(jsonArray.getJSONObject(0).get("response").toString().equals("3")){
                        helper.show("usuarioNormal.fxml", parentPane);
                    }
                    else{
                        labelError.setText("Ocurrió un error inesperado ERR02-CP"); //ERROR 02 Cambio Password Error al determinar el rol del usuario actual
                        paneError.setVisible(true);
                    }
                }
                catch (Exception e){
                    labelError.setText("Error al cambiar la contraseña, intentelo nuevamente");
                    paneError.setVisible(true);
                }
            }
        }
    }

    public void okError() {
        paneError.setVisible(false);
    }

    @FXML
    public void onEnterKey(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            btnAceptarActionPerformed();
        }
    }
}
