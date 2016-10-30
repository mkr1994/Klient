package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Encrypters.Crypter;
import sdk.Encrypters.Digester;
import sdk.Model.Book;
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
    private UserController userController;
    private AdminController adminController;
    private GuestController guestController;
    private Scanner input;

    public MainController(){
        this.userController = new UserController();
        this.adminController = new AdminController();
        this.guestController = new GuestController();
        this.input = new Scanner(System.in);
    }
    public void run() {

        int choice;

        while(true) {
        printMenu();
        choice = input.nextInt();



            switch (choice) {
                case 1:
                    //UserController
                    login();
                    break;
                case 2:
                    //AdminController
                    getAllBooks();
                    break;
                case 3:
                    //GuestController
                    break;
                case 4:
                    createNewUser();
                    break;

                default:
                    break;
            }
        }
    }

    private void getBooksFromCurriculum(){

    }

    private void getAllBooks(){

        String output = "";
        BufferedReader br = null;
        try {

            ServerConnection.openServerConnection("book", "GET");



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


        System.out.println("Output from Server .... \n");



            Gson gson = new Gson();
            int i = 1;
            ArrayList<Book> books;

            output = Crypter.encryptDecryptXOR(output);
            System.out.println(output);

            JsonReader reader = new JsonReader(new StringReader(output));
            reader.setLenient(true);

            books = gson.fromJson(reader, new TypeToken<List<Book>>(){}.getType());

            // Header i bogvisning
            System.out.printf("%-7s %-55s %-70s %-20s\n", "Nr.",  "Book title:", "Book Author", "Book ISBN", "Book Publisher: ");
            for(Book book : books){
                System.out.printf("%-7d %-55s %-70s %-20.0f\n", i,  book.getTitle(), book.getAuthor(), book.getISBN(), book.getPublisher());
                i++;
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createNewUser() {
        String firstName, lastName, username, email, password, output;
        input.nextLine();
        System.out.println("Enter your firstname: "); firstName = input.next();
        System.out.println("Enter your lastname: "); lastName = input.next();
        System.out.println("Enter your username: "); username = input.next();
        System.out.println("Enter your email: "); email = input.next();
        System.out.println("Enter your password: "); password = input.next();

        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(firstName, lastName, username, email, password, true)));

        try {
            ServerConnection.openServerConnection("user", "POST");

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

    private void login()  {
        String userName, password, output;

        Scanner input = new Scanner(System.in);
        System.out.println("PLease enter username: "); userName = input.next();

        System.out.println("Please enter password: "); password = input.next();



        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new User(userName, Digester.hashWithSalt(password))));

        try {
            ServerConnection.openServerConnection("user/login", "POST");

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

    private void printMenu(){
        System.out.println("Welcome to Bookit!\nPress 1 to login as an user\nPress 2 to login as an admin\nPress 3 continue without login\nPress 4 to create new user");
    }
}
