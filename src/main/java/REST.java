import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class REST {
    //private String ip = "35.239.78.54";
    private final String ip = "35.188.127.60";
    private final String puerto = "8080";
    private final String urlRaiz = "http://" + ip + ":" + puerto;
    JSONArray jsonArray = null;

    public JSONArray GET(String path, Boolean... correo) throws IOException {

        String getEndpoint = urlRaiz + path;
        System.out.println(getEndpoint);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(getEndpoint);
        HttpResponse response = httpclient.execute(httpget);

        try{
            //Throw runtime exception if status code isn't 200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
        }
        catch(RuntimeException re){
            System.out.println(re);
            return null;
        }
        HttpEntity entity = response.getEntity();
        if(entity != null){
            try{
                System.out.println(response);
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                if(result.length() == 0){
                    return new JSONArray();
                }
                else if (result.contains("true")){
                    return new JSONArray("[{acceso:true}]");
                }
                else if(result.contains("false")){
                    return new JSONArray("[{acceso:false}]");
                }
                else if(result.contains("@") && correo.length > 0 && correo[0] == true){
                    return new JSONArray("[{correo:" + result + "}]");
                }
                jsonArray = new JSONArray(result);
                instream.close();
                return jsonArray;
            }
            catch(Exception e){
                System.out.println(e);
                return null;
            }

        }
        else{
            return null;
        }
    }

    public JSONArray POST(String path, String... args) throws IOException {
        jsonArray = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlRaiz + path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String input = "{";
                    for(int i = 0; i < args.length; i++){
                        if(i % 2 == 0){
                            input += "\"" + args[i] + "\"";
                        }
                        else{
                            input += ":" + "\"" + args[i] + "\"" + ",";
                        }
                    }
                    input = input.substring(0, input.length()-1);
                    input += "}";
                    OutputStream os = conn.getOutputStream();
                    os.write(input.getBytes());
                    os.flush();

                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        System.out.println(conn.getResponseCode());
                        jsonArray = null;
                    }
                    else{
                        jsonArray = new JSONArray();
                    }
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    jsonArray = null;
                    e.printStackTrace();
                } catch (IOException e) {
                    jsonArray = null;
                    e.printStackTrace();
                }
            }
        });thread.start();
        return jsonArray;
    }

    public JSONArray PUT(String path, String... args) throws IOException {
        jsonArray = new JSONArray();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlRaiz + path);
                    System.out.println(url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String input = "{";
                    for(int i = 0; i < args.length; i++){
                        if(i % 2 == 0){
                            input += "\"" + args[i] + "\"";
                        }
                        else{
                            input += ":" + "\"" + args[i] + "\"" + ",";
                        }
                    }
                    input = input.substring(0, input.length()-1);
                    input += "}";
                    if(args.length <= 0){
                        input = "";
                    }
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(input);
                    out.close();
                    conn.getInputStream();
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        jsonArray = null;
                        System.out.println(conn.getResponseCode());
                    }
                    else{
                        jsonArray = new JSONArray();
                    }
                } catch (MalformedURLException e){
                    jsonArray = null;
                    e.printStackTrace();
                } catch (IOException e){
                    jsonArray = null;
                    e.printStackTrace();
                }
            }
        });thread.start();
        return jsonArray;
    }

    public JSONArray DELETE(String path, String... args) throws IOException {
        jsonArray = null;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlRaiz + path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("DELETE");
                    conn.setRequestProperty("Content-Type", "application/json");
                    String input = "{";
                    for(int i = 0; i < args.length; i++){
                        if(i % 2 == 0){
                            input += "\"" + args[i] + "\"";
                        }
                        else{
                            input += ":" + "\"" + args[i] + "\"" + ",";
                        }
                    }
                    input = input.substring(0, input.length()-1);
                    input += "}";
                    if(args.length >= 0){
                        OutputStream os = conn.getOutputStream();
                        os.write(input.getBytes());
                        os.flush();
                    }
                    conn.connect();
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        jsonArray = null;
                        System.out.println(conn.getResponseCode());
                    }
                    else{
                        jsonArray = new JSONArray();
                    }
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    jsonArray = null;
                    e.printStackTrace();
                } catch (IOException e) {
                    jsonArray = null;
                    e.printStackTrace();
                }
            }
        });thread.start();
        return jsonArray;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
