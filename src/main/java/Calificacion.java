public class Calificacion {

    private int calificacion;
    private String comentario;
    public Calificacion(int calificacion, String comentario){
        this.calificacion = calificacion;
        this.comentario = comentario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
