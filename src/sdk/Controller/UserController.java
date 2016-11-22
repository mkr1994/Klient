package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Encrypters.Crypter;
import sdk.Encrypters.Digester;
import sdk.Model.User;
import sdk.ServerConnection;
import sdk.connection.ResponseCallback;
import services.UserService;

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
    private UserService userService = new UserService();


    protected void editUser(String token){
        String output;
        User u = MainController.currentUser;
        String newInfo;
        int choice;

        // Header for showing users
        System.out.printf("%-30s %-30s %-25s %-25s\n", "Brugernavn:", "Fornavn:", "Efternavn:", "Email:");
        System.out.printf("%-30s %-30s %-25s %-25s\n", u.getUserName(), u.getFirstName(), u.getLastName(), u.getEmail());

        System.out.println("Press 1 to edit username\nPress 2 to edit firstname\nPress 3 to edit lastname\nPress 4 to edit email\nPress 5 to edit password\nPress 6 to cancel"); choice = input.nextInt();
        input.nextLine();
        System.out.println("Please enter new value: "); newInfo = input.nextLine();
        switch(choice){
            case 1: u.setUserName(newInfo);
                break;
            case 2: u.setFirstName(newInfo);
                break;
            case 3: u.setLastName(newInfo);
                break;
            case 4: u.setEmail(newInfo);
                break;
            case 5: u.setPassword(Digester.hashWithSalt(newInfo));
                break;
            default:
                System.out.println("Wrong input");

        }
        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(u));

        MainController.setPostConnection("user/25", "PUT", token, inputToServer);

    }

    protected void deleteUser(String token){
        int userId = 0;
        String output;

        if(MainController.currentUser.getUserType() == true){

                System.out.println("Pleaser enter number on the user you wish to delete: ");
                userId = input.nextInt();
        } else{
            System.out.println("Are you sure that you want to delete your account? Write \"yes\" to confirm");
            if(input.next().equals("yes"))
             userId = MainController.currentUser.getUserID();
        }

        if(userId != 0) {
            String s = "user/" + userId;

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
            } catch (Exception e) {
                System.out.println("An error occurred! " + e.getMessage());
            }
        }
    }

    protected void getUserFromToken(){

        userService.getUserFromToken(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                MainController.currentUser = data;
            }

            @Override
            public void error(int status) {

            }
        });
    }

    protected void createNewUser(){
        String firstName, lastName, username, email, password;
        System.out.println("Enter your firstname: "); firstName = input.nextLine();
        System.out.println("Enter your lastname: "); lastName = input.nextLine();
        System.out.println("Enter your username: "); username = input.next();
        System.out.println("Enter your email: "); email = input.next();
        System.out.println("Enter your password: "); password = input.next();

        User user = new User(firstName, lastName, username, email, password, false);

        userService.create(user, new ResponseCallback<String>() {
            @Override
            public void success(String data) {
                System.out.println(data);
            }

            @Override
            public void error(int status) {
                System.out.println(status);

            }
        });
    }
    protected void getAllUsers(){
         boolean s;

        userService.getAll(new ResponseCallback<ArrayList<User>>() {

            @Override
            public void success(ArrayList<User> users) {
                // Header for showing users
                System.out.printf("%-15s %-30s %-30s %-25s %-25s %-15s\n", "Bruger ID:",  "Brugernavn:", "Fornavn:", "Efternavn:", "Email:", "Admin status:");
                for(User user : users){
                    System.out.printf("%-15d %-30s %-30s %-25s %-25s %-15b\n", user.getUserID(),  user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserType());
                }
            }

            @Override
            public void error(int status) {

            }
        });
    }

}
