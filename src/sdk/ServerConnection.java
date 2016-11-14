package sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by magnusrasmussen on 25/10/2016.
 */
public class ServerConnection {

    public static URL url;

    public static HttpURLConnection conn;

    public static void openServerConnection(String path, String httpMethod){
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

    public static void openServerConnection(String path, String httpMethod, String token){
        try {
            url = new URL("http://localhost:8080/server2_0_war_exploded/"+path);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);

            conn.addRequestProperty("authorization", token);


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
