package sdk.Controller;

import com.google.gson.Gson;
import sdk.Encrypters.Crypter;
import sdk.Encrypters.Digester;
import sdk.Model.User;
import sdk.ServerConnection;

import java.io.*;
import java.util.Scanner;

import static sdk.ServerConnection.conn;

/**
 * Created by magnusrasmussen on 25/10/2016.
 */
public class MainController {
    private BookController bookController;
    private AdminController adminController;
    private UserController userController;
    private Scanner input;
    private Gson gson;
    public static User currentUser;
    public String token;

    public MainController(){
        this.bookController = new BookController();
        this.adminController = new AdminController();
        this.userController = new UserController();
        this.input = new Scanner(System.in);
        this.gson = new Gson();
    }
    public void run() {

        while(currentUser == null) {

            printMenu();
            switch(input.nextInt()){
                case 1: if(login() && currentUser.getUserType()==false){
                    showUserSwitch();
                } else if(currentUser.getUserType() == true){
                    System.out.println("You are not an admin!");
                    userController.editUser(token);
                } else{
                    System.out.println("Forkert brugernavn eller adgagngskode - prøv igen!");
                }
                    break;
                case 2: if(login() && currentUser.getUserType()==true) {
                    showAdminSwitch();
                }
                    break;
                case 3: showGuestSwitch();
                    break;
                case 4: userController.createNewUser();
                    break;

            }
        }
    }

    private void showUserSwitch() {

        int choice;
                System.out.println("Welcome to usermenu \nPress 1 to find af book\nPress 2 to update your info\nPress 3 to delete your account" +
                "\nTast 4 for at logge ud  ");

        choice = input.nextInt();

        switch (choice) {
            case 1: bookController.getBooksFromCurriculum();
                break;
            case 2: userController.editUser(token);
                break;
            case 3:
                break;
            case 4:
                break;


        }

    }

    private void showAdminSwitch() {

        System.out.println("Welcome to admin menu. \nPress 1 to view all users\nPress 2 to delete an user\nPress 3 to create new book" +
                "\nPress 4 to quit  ");

        switch (input.nextInt()) {
            case 1: userController.getAllUsers(token);
                break;
            case 2: userController.deleteUser(token);
                break;
            case 3: bookController.createNewBook(token);
                break;
            case 4: bookController.getAllBooks();
                break;


        }

    }

    private void showGuestSwitch() {
        System.out.println("Welcome guest \nPress 1 to find price info on a book\nPress 2 to signup as an user");

        int choice = input.nextInt();

        switch (choice) {
            case 1:
                bookController.getBooksFromCurriculum();
                break;
            case 2: userController.createNewUser();
                break;
        }


    }


    public static String setConnection(String path, String method){
        String output = null;
        BufferedReader br = null;
        try {

            ServerConnection.openServerConnectionWithToken(path, method);
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
        return output;
    }



    private boolean login()  {
        String userName, password, output;
        boolean loginOk = false;

        Scanner input = new Scanner(System.in);
        System.out.println("Please enter username: "); userName = input.nextLine();

        System.out.println("Please enter password: "); password = input.nextLine();


        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(userName, Digester.hashWithSalt(password))));

        try {
            ServerConnection.openServerConnectionWithToken("user/login", "POST");

        OutputStream os = conn.getOutputStream();
        os.write(inputToServer.getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            System.out.println("Failed : HTTP error code : ");
                  //  + conn.getResponseCode());
        } else{
            loginOk = true;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        while ((output = br.readLine()) != null) {
            token = output;

        }

        conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        userController.getUserFromToken(token);

        return loginOk;
    }

    private void printMenu(){
        System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 continue without login\nPress 4 to create new user");
    }

    public String getToken(){
        return token;
    }
}
