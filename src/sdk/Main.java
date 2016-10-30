package sdk;

import com.google.gson.Gson;
import sdk.Controller.MainController;
import sdk.Encrypters.Crypter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by magnusrasmussen on 24/10/2016.
 */
public class Main  {
    public static void main(String[] args) throws IOException {

        MainController mainController = new MainController();

        mainController.run();
    }
}

