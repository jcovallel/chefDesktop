public class UsuarioEntity {
    private static String nombre;
    private static String correo;
    private static String contrasena;
    private static UsuarioEntity usuarioEntity;

    private UsuarioEntity(String nombre){
        this.nombre = nombre;
        this.correo = correo;
    }
    public static UsuarioEntity getUsuario(String nombre){
        if(usuarioEntity == null){
            usuarioEntity = new UsuarioEntity(nombre);
        }
        return usuarioEntity;
    }
    public static String getNombre(){
        return nombre;
    }
    public void destroy(){
        usuarioEntity = null;
    }
}
