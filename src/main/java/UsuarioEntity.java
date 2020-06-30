public class UsuarioEntity {
    private static String nombre;
    private static String correo;
    private static String contrasena;
    private static Integer rol;
    private static UsuarioEntity usuarioEntity;

    private UsuarioEntity(String nombre, Integer rol){
        this.nombre = nombre;
        this.rol = rol;
        this.correo = correo;
    }
    public static UsuarioEntity getUsuario(String nombre, Integer rol){
        if(usuarioEntity == null){
            usuarioEntity = new UsuarioEntity(nombre, rol);
        }
        return usuarioEntity;
    }
    public static String getNombre(){
        return nombre;
    }
    public static Integer getRol(){
        return rol;
    }
    public void destroy(){
        usuarioEntity = null;
    }
}
