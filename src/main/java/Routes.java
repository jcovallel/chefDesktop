import java.util.HashMap;

public class Routes{
    enum routesName{
        GET_USUARIOS,
        GET_REVIEWS_ADMIN,
        GET_REVIEWS_USUARIOS,
        GET_EXCEL_DISPONIBILIDAD,
        GET_EXCEL_COMENTARIOS,
        GET_EMAIL,
        GET_PASS,
        MODIFY_RESTAURANTE,
        MODIFY_MENU,
        MODIFY_DISPONIBILIDAD,
        MODIFY_USUARIO,
        CREATE_USUARIO,
        DELETE_USUARIO,
        DELETE_MENU,
        DELETE_COMENTS,
        DELETE_DMODEL,
        DELETE_DHMODEL,
        DELETE_DISPOMODEL,
        SEND_EMAIL,
        UPLOAD_IMAGE,
        CREATE_HOURS,
        CREATE_MENU,
        GET_ROL,
        GET_USUARIOS_ROL3,
        GET_MENUS
    }
    private static HashMap<String, String> routesHash;
    private static Routes routesEntity;


    // Constructor de rutas. Para agregar una nueva ruta se debe poner el nombre representativo de la ruta EN MAYUSCULAS
    // tanto en este constructor como en la clase enum de arriba. El segundo parámetro es la ruta en cuestión, empezando
    // siempre con un /chef, y terminando siempre con un slash (/)
    private Routes(){
        routesHash = new HashMap<>();
        routesHash.put("GET_USUARIOS", "/chef/getusers/");
        routesHash.put("GET_REVIEWS_ADMIN", "/chef/admin/review/");
        routesHash.put("GET_REVIEWS_USUARIOS", "/chef/user/review/");
        routesHash.put("GET_EXCEL_DISPONIBILIDAD", "/chef/download_excel/");
        routesHash.put("GET_EXCEL_COMENTARIOS", "/chef/download_excel_comen/");
        routesHash.put("GET_EMAIL", "/chef/getmail/");
        routesHash.put("GET_PASS", "/chef/getpass/");
        routesHash.put("MODIFY_RESTAURANTE", "/chef/modifyinfoadmi/");
        routesHash.put("MODIFY_DISPONIBILIDAD", "/chef/disponibilidad/");
        routesHash.put("MODIFY_USUARIO", "/chef/modifydatausers/");
        routesHash.put("MODIFY_MENU", "/chef/modifymenu/");
        routesHash.put("CREATE_USUARIO", "/chef/createuser/");
        routesHash.put("CREATE_MENU", "/chef/createmenu/");
        routesHash.put("DELETE_USUARIO", "/chef/deleteuser/");
        routesHash.put("DELETE_MENU", "/chef/deletemenu/");
        routesHash.put("DELETE_COMENTS", "/chef/deletecoment/");
        routesHash.put("DELETE_DMODEL", "/chef/deletedmodel/");
        routesHash.put("DELETE_DHMODEL", "/chef/deletedhmodel/");
        routesHash.put("DELETE_DISPOMODEL", "/chef/deletedispomodel/");
        routesHash.put("SEND_EMAIL", "/chef/sendmail/");
        routesHash.put("UPLOAD_IMAGE", "/chef/uploadmenu/");
        routesHash.put("CREATE_HOURS", "/chef/createhours/");
        routesHash.put("GET_ROL", "/chef/getrol/");
        routesHash.put("GET_USUARIOS_ROL3", "/chef/getusersmobile/");
        routesHash.put("GET_MENUS", "/chef/getmenus/");
    }

    public static Routes getRoutesClass(){
        if(routesEntity == null){
            routesEntity = new Routes();
        }
        return routesEntity;
    }

    public String getRoute(routesName path, String... params){
        String outputRoute = routesHash.get(path.toString());
        if(params.length == 0){
            outputRoute = outputRoute.replaceAll(" ", "%20");
            return outputRoute;
        }
        for(String paramsIterator : params){
            outputRoute += paramsIterator + "/";
        }
        outputRoute = outputRoute.substring(0, outputRoute.length() - 1);
        outputRoute = outputRoute.replaceAll(" ", "%20");
        return outputRoute;
    }

}
