public class Calificacion {

    private String cali;
    private String come;
    private String name;
    private String cel;
    private String addr;
    public Calificacion(String calificacion, String comentario, String nombre, String celular, String correo){
        this.cali = calificacion;
        this.come = comentario;
        this.name = nombre;
        this.cel = celular;
        this.addr = correo;

    }

    public String getCali() {
        return cali;
    }

    public String getCome() {
        return come;
    }

    public void setCali(String calificacion) {
        this.cali = calificacion;
    }

    public void setCome(String comentario) {
        this.come = comentario;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public void setCel(String celular) {
        this.cel = celular;
    }

    public void setAddr(String correo) {
        this.addr = correo;
    }

    public String getName() {
        return name;
    }

    public String getCel() {
        return cel;
    }

    public String getAddr() {
        return addr;
    }

    @Override
    public String toString(){
        return "[" + this.cali + " " + this.come + " " + this.name + " " + this.cel + " " + this.addr + "]";
    }
}
