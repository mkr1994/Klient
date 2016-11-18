package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Encrypters.Crypter;
import sdk.Model.User;
import sdk.ServerConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static sdk.ServerConnection.conn;

/**
 * Created by magnusrasmussen on 29/10/2016.
 */
public class UserController {

    private Scanner input = new Scanner(System.in);

    private Gson gson = new Gson();


    protected void deleteUser(String token){
        int userId = 0;
        String output;
        getAllUsers(token);
        System.out.println("Pleaser enter number on the user you wish to delete: ");
        userId = input.nextInt();




        String s = "user/"+userId;

        try {
            ServerConnection.openServerConnectionWithToken(s, "DELETE", token);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    protected void getUserFromToken(String token){

        String output = null;
        BufferedReader br = null;
        try {

            ServerConnection.openServerConnectionWithToken("user/fromToken", "GET", token);


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


           output = br.readLine();
            output = Crypter.encryptDecryptXOR(output);
            MainController.currentUser = (gson.fromJson(output, User.class));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    protected void createNewUser() {
        String firstName, lastName, username, email, password, output;
       // input.nextLine();
        System.out.println("Enter your firstname: "); firstName = input.nextLine();
        System.out.println("Enter your lastname: "); lastName = input.nextLine();
        System.out.println("Enter your username: "); username = input.next();
        System.out.println("Enter your email: "); email = input.next();
        System.out.println("Enter your password: "); password = input.next();

        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(firstName, lastName, username, email, password, false)));

        try {
            ServerConnection.openServerConnectionWithToken("user", "POST");

            OutputStream os = conn.getOutputStream();
            os.write(inputToServer.getBytes());
            os.flush();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    protected void getAllUsers(String token){
        String output = null;
        BufferedReader br = null;
        try {

            ServerConnection.openServerConnectionWithToken("user", "GET", token);


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            StringBuilder builder = new StringBuilder();
            String aux = "";

            while ((aux = br.readLine()) != null) {
                builder.append(aux + "\n");
            }
            output = builder.toString();
            output = Crypter.encryptDecryptXOR(output);

        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonReader reader = new JsonReader(new StringReader(output));
        reader.setLenient(true);


        ArrayList<User> users = gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());

        // Header i bogvisning
        System.out.printf("%-15s %-30s %-30s %-25s %-25s %-15s\n", "Bruger ID:",  "Brugernavn:", "Fornavn:", "Efternavn:", "Email:", "Admin status:");
        for(User user : users){
            System.out.printf("%-15d %-30s %-30s %-25s %-25s %-15b\n", user.getUserID(),  user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserType());
        }
    }
}
