import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

public class MenuModel {
    private CheckBox remark;
    private final SimpleStringProperty nombreMenu;


     MenuModel(Boolean value, String nombreMenu) {
         this.remark = new CheckBox();
         this.nombreMenu = new SimpleStringProperty(nombreMenu);
        if(value){
            this.remark.setSelected(value);
        }else {
            this.remark.setSelected(value);
        }
    }

    public String getNombreMenu() {
        return nombreMenu.get();
    }
    public void setNombreMenu(String fName) {
        nombreMenu.set(fName);
    }


    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }
}
