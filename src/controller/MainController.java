package controller;

import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import sdk.model.CachedData;
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
    public static User currentUser;
    public static String token;
    private Connection connection;
    public static long startTime;
    private CachedData cachedData;

    public MainController() {
        this.bookController = new BookController();
        this.userController = new UserController();
        this.input = new Scanner(System.in);
        this.connection = new Connection();
        this.cachedData = new CachedData();
    }

    public void run() {

        while (currentUser == null) {

            printMenu(1);
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
                            startTime = System.currentTimeMillis();
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
                    System.err.println("Please enter a valid number!");
                }
            } catch (Exception e) {
                System.err.println("A serious error occurred! Please login again! Error message:\n " + e.getMessage());

            } finally {
                cachedData.clearCache();
                logout();
                input.nextLine();
            }

        }

    }

    private void showUserSwitch() {
        int choice;
        do {
            printMenu(3);
            choice = input.nextInt();

            switch (choice) {
                case 1:
                    bookController.getBooksFromCurriculum();
                    break;
                case 2:
                    userController.editUser();
                    break;
                case 3:
                    userController.deleteUser(cachedData);
                    break;
                case 4:
                    logout();
                    break;
            }
        }while(choice > 4);

    }

    private void showAdminSwitch() {
        int choice;
        do {
            printMenu(2);
            choice = input.nextInt();
            switch (choice) {
                case 1:
                    userController.getAllUsers(cachedData);
                    break;
                case 2:
                    userController.deleteUser(cachedData);
                    break;
                case 3:
                    bookController.createNewBook();
                    break;
                case 4:
                    bookController.getAllBooks(cachedData);
                    break;
                case 5:
                    bookController.createNewCurriculum();
                    break;
                case 6:
                    cachedData.clearCache();
                    logout();
                    break;
            }
        } while (choice > 6);

    }

    private void showGuestSwitch() {
        int choice;
        do {
            printMenu(4);
            choice = input.nextInt();

            switch (choice) {
                case 1:
                    bookController.getBooksFromCurriculum();
                    break;
                case 2:
                    userController.createNewUser();
                    break;
                default:
                    break;
            }
        } while (choice > 3);

    }

    /**
     * Login method
     * @param responseCallback
     */
    private void login(final ResponseCallback<String> responseCallback) {

        HttpPost postRequest = new HttpPost(Connection.serverURL + "user/login");

        String userName, password;
        input.nextLine();

        System.out.println("Please enter username: ");
        userName = input.nextLine();
        System.out.println("Please enter password: ");
        password = input.nextLine();
        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(userName, Digester.hashWithSalt(password))));

        try {
            StringEntity loginInfo = new StringEntity(inputToServer);
            postRequest.setEntity(loginInfo);
            postRequest.setHeader("Content-Type", "application/json");
            this.connection.execute(postRequest, new ResponseParser() {

                public void payload(String json) {
                    responseCallback.success(json);
                }

                public void error(int status) {
                    responseCallback.error(status);
                }
            });


        } catch (UnsupportedEncodingException e) {
            System.err.println("An error occurred!");
        }
    }

    private void printMenu(int i) {
        if (i == 1) {
            System.out.println("__________               __   .___  __   ");
            System.out.println("\\______   \\ ____   ____ |  | _|   |/  |_ ");
            System.out.println(" |    |  _//  _ \\ /  _ \\|  |/ /   \\   __\\");
            System.out.println(" |    |   (  <_> |  <_> )    <|   ||  |  ");
            System.out.println(" |______  /\\____/ \\____/|__|_ \\___||__|  ");
            System.out.println("        \\/                   \\/          ");
            System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 to continue without login\nPress 4 to create new user");
        } else if (i == 2)
        {
            System.out.println("Welcome to admin menu. \nPress 1 to view all users\nPress 2 to delete an user\nPress 3 to create a new book" +
                    "\nPress 4 to view all books\nPress 5 to create new Curriculum\nPress 6 to logout");
        } else if (i == 3)
        {
            System.out.println("Welcome to user menu \nPress 1 to find a book\nPress 2 to update your info\nPress 3 to delete your account" +
                    "\nPress 4 to log out ");
        } else if (i == 4)
        {
            System.out.println("Welcome guest \nPress 1 to find price info on a book\nPress 2 to sign up as an user\nPress 3 to return to main menu");
        }
    }
    public static void logout() {
        currentUser = null;
        token = null;
    }


}
