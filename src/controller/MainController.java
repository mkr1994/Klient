package controller;

import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import sdk.encrypters.Crypter;
import sdk.encrypters.Digester;
import sdk.model.User;
import sdk.connection.Connection;
import sdk.connection.ResponseCallback;
import sdk.connection.ResponseParser;
import view.BookController;
import view.UserController;

import java.io.*;
import java.util.Scanner;

/**
 * Created by magnusrasmussen on 25/10/2016.
 */
public class MainController {
    private BookController bookController;
    private UserController userController;
    private Scanner input;
    private Gson gson;
    public static User currentUser;
    public static String token;
    private Connection connection;

    public MainController(){
        this.bookController = new BookController();
        this.userController = new UserController();
        this.input = new Scanner(System.in);
        this.gson = new Gson();
        this.connection = new Connection();
    }
    public void run() {

    while (currentUser == null) {


            printMenu();
        try {
            int choice = input.nextInt();


            if (choice == 3 || choice == 4) {
                switch (choice) {
                    case 3:
                        showGuestSwitch();
                        break;
                    case 4:
                        userController.createNewUser();
                        break;

                }
            } else if (choice == 1 || choice == 2) {
                login(new ResponseCallback<String>() {
                    @Override
                    public void success(String data) {
                        token = data;
                        userController.getUserFromToken();
                        switch (choice) {
                            case 1:
                                if (currentUser.getUserType() == false) {
                                    do {
                                        showUserSwitch();
                                    } while (currentUser != null);
                                }
                                break;
                            case 2:
                                if (currentUser.getUserType() == true) {
                                    do {
                                        showAdminSwitch();
                                    } while (currentUser != null);
                                }
                                break;
                        }
                    }

                    @Override
                    public void error(int status) {
                        System.out.println("Error, status: " + status);

                    }
                });
            } else {
                System.out.println("Please enter a valid number!");
            }
        }catch(Exception e){
            System.out.println("A serious error occurred!");
            logout();
            input.next();
        }
    }

    }

    private void showUserSwitch() {

        int choice;
                System.out.println("Welcome to usermenu \nPress 1 to find af book\nPress 2 to update your info\nPress 3 to delete your account" +
                "\nPress 4 to log out: ");

        choice = input.nextInt();

        switch (choice) {
            case 1: bookController.getBooksFromCurriculum();
                break;
            case 2: userController.editUser();
                break;
            case 3: userController.deleteUser();
                break;
            case 4: logout();
                break;
        }

    }

    private void showAdminSwitch() {

        System.out.println("Welcome to admin menu. \nPress 1 to view all users\nPress 2 to delete an user\nPress 3 to create new book" +
                "\nPress 4 to view all books\nPress 5 to logout:  ");

        switch (input.nextInt()) {
            case 1: userController.getAllUsers();
                break;
            case 2: userController.deleteUser();
                break;
            case 3: bookController.createNewBook();
                break;
            case 4: bookController.getAllBooks();
                break;
            case 5: logout();
                break;


        }

    }

    private void showGuestSwitch() {
        System.out.println("Welcome guest \nPress 1 to find price info on a book\nPress 2 to signup as an user\nPress 3 to return to main menu");

        int choice = input.nextInt();

        switch (choice) {
            case 1:
                bookController.getBooksFromCurriculum();
                break;
            case 2: userController.createNewUser();
                break;
            default: break;
        }


    }

    public void login(final ResponseCallback<String> responseCallback){

        HttpPost postRequest = new HttpPost(Connection.serverURL + "user/login");

        String userName, password;
        boolean loginOk = false;
        input.nextLine();

        System.out.println("Please enter username: "); userName = input.nextLine();
        System.out.println("Please enter password: "); password = input.nextLine();
        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(userName, Digester.hashWithSalt(password))));

        try {
            StringEntity loginInfo = new StringEntity(inputToServer);
            postRequest.setEntity(loginInfo);
            postRequest.setHeader("Content-Type", "application/json");
            this.connection.execute(postRequest, new ResponseParser() {

                public void payload(String json) {
                   // if(json != null) {
                        responseCallback.success(json);
                   // }
                }
                public void error(int status) {
                    responseCallback.error(status);
                }
            });


        } catch (UnsupportedEncodingException e) {
            System.out.println("An error occurred!");
        }
    }

    private void printMenu(){
        System.out.println("__________               __   .___  __   ");
        System.out.println("\\______   \\ ____   ____ |  | _|   |/  |_ ");
        System.out.println(" |    |  _//  _ \\ /  _ \\|  |/ /   \\   __\\");
        System.out.println(" |    |   (  <_> |  <_> )    <|   ||  |  ");
        System.out.println(" |______  /\\____/ \\____/|__|_ \\___||__|  ");
        System.out.println("        \\/                   \\/          ");
        System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 to continue without login\nPress 4 to create new user");
    }

    public static void logout(){
        currentUser = null;
        token = null;
    }

    public String getToken(){
        return token;
    }
}
