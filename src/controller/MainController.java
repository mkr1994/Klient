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
import view.BookView;
import view.UserView;

import java.io.*;
import java.util.Scanner;

/**
 * Controlling login function, and which view to show the user
 * Created by magnusrasmussen on 25/10/2016.
 */
public class MainController {
    private BookView bookView;
    private UserView userView;
    private Scanner input;
    public static User currentUser; // Current user data
    public static String token; // Accesstoken received at successful login
    private Connection connection;
    public static long startTime; // Used to control when to refresh cache
    private CachedData cachedData; // Storing cached data local in RAM memory

    public MainController() {
        this.bookView = new BookView();
        this.userView = new UserView();
        this.input = new Scanner(System.in);
        this.connection = new Connection();
        this.cachedData = new CachedData();
    }

    /**
     * run method controlling the execution of the program and which menues to show.
     */
    public void run() {

        while (currentUser == null) {

            printMenu(1); //Print welcome menu
            try {
                int choice = input.nextInt();


                /**
                 * These methods doesn't need a login
                 */
                if (choice == 3 || choice == 4) {
                    switch (choice) {
                        case 3:
                            showGuestSwitch();
                            break;
                        case 4:
                            userView.createNewUser();
                            break;

                    }
                } else if (choice == 1 || choice == 2) {
                    /**
                     * Depending on what menu the user has tried to login to, the given menu will be shown if allowed.
                     */
                    login(new ResponseCallback<String>() {
                        @Override
                        public void success(String data) {
                            startTime = System.currentTimeMillis(); //Start timer used for caching
                            token = data; //Set token.
                            userView.getUserFromToken(); //get userinfo from token
                            switch (choice) {
                                case 1:
                                    if (currentUser.getUserType() == false) {
                                        do {
                                            showUserSwitch();
                                        } while (currentUser != null); //Run as long as the user hasn't logged out.
                                    }
                                    break;
                                case 2:
                                    if (currentUser.getUserType() == true) {
                                        do {
                                            showAdminSwitch();
                                        } while (currentUser != null); //Run as long as the user hasn't logged out.
                                    } else
                                        System.out.println("Sorry, you are not an admin! ");
                                    break;
                            }
                        }

                        @Override
                        public void error(int status) {
                            System.err.println("Error, status: " + status);

                        }
                    });
                } else {
                    System.err.println("Please enter a valid number!");
                }
            } catch (Exception e) { // System wide catch. One misstep will result in  logout
                System.err.println("A serious error occurred! Please login again! Error message:\n " + e.getMessage()); // Not that serious, probably just inputmismatch

            } finally { //Finally clause, so all session data always will be cleared.
                cachedData.clearCache(); //Clear local cache.
                logout(); // As the name states, logout by clearing other local data such as currentuser.
            }
            input.nextLine();

        }

    }

    /**
     * User menu
     */
    private void showUserSwitch() {
        int choice;
        do {
            printMenu(3); //print user menu
            choice = input.nextInt();

            switch (choice) {
                case 1:
                    bookView.getBooksFromCurriculum();
                    break;
                case 2:
                    userView.editUser();
                    break;
                case 3:
                    userView.deleteUser(cachedData);
                    break;
                case 4:
                    currentUser = null;
                    break;
                default:
                    System.err.println("Please enter valid number\n");
                    break;
            }
        } while (choice > 4);

    }

    /**
     * Admin menu.
     */
    private void showAdminSwitch() {
        int choice;
        do {
            printMenu(2); //Print admin menu
            choice = input.nextInt();
            switch (choice) {
                case 1:
                    userView.getAllUsers(cachedData);
                    break;
                case 2:
                    userView.deleteUser(cachedData);
                    break;
                case 3:
                    bookView.createNewBook();
                    break;
                case 4:
                    bookView.getAllBooks(cachedData);
                    break;
                case 5:
                    bookView.createNewCurriculum();
                    break;
                case 6:
                    currentUser = null;
                    break;
            }
        } while (choice > 6);

    }

    /**
     * Guest menu
     */
    private void showGuestSwitch() {
        int choice;
        do {
            printMenu(4);
            choice = input.nextInt();

            switch (choice) {
                case 1:
                    bookView.getBooksFromCurriculum();
                    break;
                case 2:
                    userView.createNewUser();
                    break;
                case 3: break;
                default:
                    System.err.println("Please enter af valid number\n");
                    break;
            }
        } while (choice > 3);

    }

    /**
     * Login method
     *
     * @param responseCallback
     */
    private void login(final ResponseCallback<String> responseCallback) {
        String userName, password;
        input.nextLine();

        HttpPost postRequest = new HttpPost(Connection.serverURL + "user/login");
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
            System.err.println("An error occurred!\n");
        }
    }

    /**
     * Various TUI menues
     *
     * @param i
     */
    private void printMenu(int i) {
        if (i == 1) {
            System.out.println("__________               __   .___  __   ");
            System.out.println("\\______   \\ ____   ____ |  | _|   |/  |_ ");
            System.out.println(" |    |  _//  _ \\ /  _ \\|  |/ /   \\   __\\");
            System.out.println(" |    |   (  <_> |  <_> )    <|   ||  |  ");
            System.out.println(" |______  /\\____/ \\____/|__|_ \\___||__|  ");
            System.out.println("        \\/                   \\/          ");
            System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 to continue without login\nPress 4 to create new user");
        } else if (i == 2) {
            System.out.println("Welcome to admin menu. \nPress 1 to view all users\nPress 2 to delete an user\nPress 3 to create a new book" +
                    "\nPress 4 to view all books\nPress 5 to create new Curriculum\nPress 6 to logout");
        } else if (i == 3) {
            System.out.println("Welcome to user menu \nPress 1 to find a book\nPress 2 to update your info\nPress 3 to delete your account" +
                    "\nPress 4 to log out ");
        } else if (i == 4) {
            System.out.println("Welcome guest \nPress 1 to find price info on a book\nPress 2 to sign up as an user\nPress 3 to return to main menu");
        }
    }

    /**
     * Currentuser logout. Remember to add new variables used in session.
     */
    public static void logout() {
        currentUser = null;
        token = null;
    }


}
