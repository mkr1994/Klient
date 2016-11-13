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
import java.util.Arrays;
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
    private Gson gson;

    public MainController(){
        this.userController = new UserController();
        this.adminController = new AdminController();
        this.guestController = new GuestController();
        this.input = new Scanner(System.in);
        this.gson = new Gson();
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
                    getBooksFromCurriculum();
                    break;
                case 4:
                    createNewUser();
                    break;

                case 5:
                    getAllCurriculums();
                    break;

                default:
                    break;
            }
        }
    }



    private String setConnection(String path, String method){
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
    private ArrayList<Curriculum> getAllCurriculums(){

            String output = setConnection("curriculum", "GET");

         JsonReader reader = new JsonReader(new StringReader(output));
        reader.setLenient(true);
            int i = 1;
            ArrayList<Curriculum> curriculums;
            curriculums = gson.fromJson(reader, new TypeToken<List<Curriculum>>(){}.getType());
/*
            // Header i bogvisning
            System.out.printf("%-7s %-20s %-20s %-20s\n", "Id.",  "School:", "Education:", "Semester:");
            for(Curriculum c : curriculums){
                System.out.printf("%-7d %-20s %-20s %-20d\n", i,  c.getSchool(), c.getEducation(), c.getSemester());
                i++;
            }
*/
            return curriculums;
    }

    private void getBooksFromCurriculum(){
        int curriculumID;
        int i = 1, j= 1, choice;
        ArrayList<Curriculum> curriculumArrayList = getAllCurriculums();
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> b = new ArrayList<>();
        for(Curriculum c : curriculumArrayList){
                if(!strings.contains(c.getSchool())){
                    System.out.println("Nr:  "+ j + " " + c.getSchool());
                    strings.add(c.getSchool());
                    j++;
                } else if (strings.size() == 0){
                    strings.add(c.getSchool());
                }
        }
        j = 1;
        System.out.println("Indtast nr. på det uddannelsessted du er indskrevet:");
        choice = input.nextInt();
        choice--;
        int finalChoice = choice;
        curriculumArrayList.removeIf(Curriculum -> !Curriculum.getSchool().contains(strings.get(finalChoice)));
        strings.clear();
        for(Curriculum c : curriculumArrayList){
            if(!strings.contains(c.getEducation())){
                System.out.println("Nr: " + j + " "+ c.getEducation());
                strings.add(c.getEducation());
                j++;
            } else if (strings.size() == 0){
                strings.add(c.getEducation());
            }
        }
        j = 1;
        System.out.println("Indtast nr. på din uddannelse: ");
        choice = input.nextInt();
        choice--;
        int finalChoice2 = choice;
        curriculumArrayList.removeIf(Curriculum -> !Curriculum.getEducation().contains(strings.get(finalChoice2)));



        for(Curriculum c : curriculumArrayList){
            if(!b.contains(c.getSemester())){
                System.out.printf("\nNr: %d: " + c.getSemester(), j);
                b.add(c.getSemester());
                j++;
            } else if (b.size() == 0){
                b.add(c.getSemester());
            }
        }
        System.out.println("\nIndtast venligst nr. på hvilket semester du går på:");
        curriculumID = input.nextInt();
        curriculumID--;
        curriculumID = curriculumArrayList.get(curriculumID).getCurriculumID();


        String s = "curriculum/"+curriculumID+"/books";


            String output = setConnection(s,"GET");

            JsonReader reader = new JsonReader(new StringReader(output));
            reader.setLenient(true);

        ArrayList<Book> books = gson.fromJson(reader, new TypeToken<List<Book>>(){}.getType());

            // Header i bogvisning
            System.out.printf("%-7s %-55s %-70s %-20s\n", "Nr.",  "Book title:", "Book Author", "Book ISBN", "Book Publisher: ");
            for(Book book : books){
                System.out.printf("%-7d %-55s %-70s %-20.0f\n", i,  book.getTitle(), book.getAuthor(), book.getISBN(), book.getPublisher());
                i++;
            }


    }

    private void getAllBooks(){
            int i = 1;
            String output = setConnection("book", "GET");
            System.out.println(output);
            JsonReader reader = new JsonReader(new StringReader(output));
            reader.setLenient(true);

             ArrayList<Book>books = gson.fromJson(reader, new TypeToken<List<Book>>(){}.getType());

            // Header i bogvisning
                System.out.printf("%-7s %-55s %-80s %-25s %-25s\n", "Nr.",  "Book title:", "Book Author:", "Book ISBN:", "Book Price Amazon:");
                for(Book book : books){
                    System.out.printf("%-7d %-55s %-80s %-25.0f %-25.2f\n", i,  book.getTitle(), book.getAuthor(), book.getISBN(), book.getPriceAB());
                    i++;
            }

    }

    private void createNewUser() {
        String firstName, lastName, username, email, password, output;
        input.nextLine();
        System.out.println("Enter your firstname: "); firstName = input.nextLine();
        System.out.println("Enter your lastname: "); lastName = input.nextLine();
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
