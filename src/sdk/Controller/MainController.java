package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Encrypters.Crypter;
import sdk.Encrypters.Digester;
import sdk.Model.Book;
import sdk.Model.Curriculum;
import sdk.Model.User;
import sdk.ServerConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
    private String userToken;

    public MainController(){
        this.bookController = new BookController();
        this.adminController = new AdminController();
        this.userController = new UserController();
        this.input = new Scanner(System.in);
        this.gson = new Gson();
    }
    public void run() {

        login();
        userController.getAllUsers(userToken);
        int choice;

        while(currentUser == null) {


            System.out.println("Velkommen til Bookit.\nTast 1 for at logge ind som bruger\nTast 2 for at logge ind som administrator\nTast 3 for at fortsætte som gæst\nTast 4 for at oprette dig som ny bruger");

            choice = input.nextInt();
            switch(choice){
                case 1: showUserSwitch();
                    break;
                case 2: showAdminSwitch();
                    break;
                case 3: showGuestSwitch();
                    break;
                case 4: userController.createNewUser();


            }
            if (login()) {
                printMenu();
                choice = input.nextInt();
                switch (choice) {
                    case 1:
                        //BookController
                        login();
                        break;
                    case 2:
                        //AdminController
                        //getAllBooks();
                        break;
                    case 3:
                        //UserController
                     //   getBooksFromCurriculum();
                        break;
                    case 4:
                        //createNewUser();
                        break;

                    case 5:
                     //   getAllCurriculums();
                        break;

                    default:
                        break;
                }
            }
            System.out.println("Forkert brugernavn eller adgagngskode - prøv igen!");
        }
    }

    private void showUserSwitch() {

        int choice;

        System.out.println("Velkommen til Brugermenuen. \nTast 1 for at se en pensumliste\nTast 2 for at opdatere dine oplysninger\nTast 3 for at slette din konto" +
                "\nTast 4 for at logge ud  ");

        choice = input.nextInt();

        switch (choice) {
            case 1: bookController.getBooksFromCurriculum();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;


        }

    }

    private void showAdminSwitch() {

    }

    private void showGuestSwitch() {

    }


    public static String setConnection(String path, String method){
        String output = null;
        BufferedReader br = null;
        try {

            ServerConnection.openServerConnection(path, method);
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
            ServerConnection.openServerConnection("user/login", "POST");

        OutputStream os = conn.getOutputStream();
        os.write(inputToServer.getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        } else{
            loginOk = true;

        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));


        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            userToken = output;
        }




        conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loginOk;
    }

    private void printMenu(){
        System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 continue without login\nPress 4 to create new user");
    }

    public String getUserToken(){
        return userToken;
    }
}
