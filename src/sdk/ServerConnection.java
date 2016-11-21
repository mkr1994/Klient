package sdk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by magnusrasmussen on 25/10/2016.
 */
public class ServerConnection {

    public static URL url;

    public static HttpURLConnection conn;

    public static void openServerConnectionWithoutToken(String path, String httpMethod){
        try {
            url = new URL("http://localhost:8080/server2_0_war_exploded/"+path);

        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);


        conn.setRequestMethod(httpMethod);
        conn.setRequestProperty("Content-Type", "application/json");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void openServerConnectionWithToken(String path, String httpMethod, String token){
        try {
            url = new URL("http://localhost:8080/server2_0_war_exploded/"+path);

            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("authorization", token);
            conn.setDoOutput(true);


            conn.setRequestMethod(httpMethod);
            conn.setRequestProperty("Content-Type", "application/json");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void clodeServerConnection(){
        conn.disconnect();
    }




}
