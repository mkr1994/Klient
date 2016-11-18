package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Encrypters.Crypter;
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
 * Created by magnusrasmussen on 29/10/2016.
 */
public class BookController  {

    private Scanner input;
    private Gson gson;
    public BookController(){
        this.input = new Scanner(System.in);
        this.gson = new Gson();
    }


    protected void addBookToCurriculum(int curriculumID){




    }

    protected void createNewBook(){
        String output;
        String publisher, title, author;
        double priceAB, priceSAXO, priceCDON, ISBN;
        int version;
        int curriculumID = extractCurriculumID();
        System.out.println("Enter title: "); title = input.nextLine();
        System.out.println("Enter author: "); author = input.nextLine();
        System.out.println("Enter publisher: "); publisher = input.next();
        System.out.println("Enter version: "); version = input.nextInt();
        System.out.println("Enter book ISBN:"); ISBN = input.nextDouble();
        System.out.println("Enter price at Amazon: "); priceAB = input.nextDouble();
        System.out.println("Enter price at SAXO:"); priceSAXO = input.nextDouble();
        System.out.println("Enter price at CDON:"); priceCDON = input.nextDouble();

        String inputToServer = Crypter.encryptDecryptXOR(new Gson().toJson(new Book(publisher, title, author, version, ISBN, priceAB, priceSAXO, priceCDON, curriculumID)));


        try {
            ServerConnection.openServerConnectionWithToken("book", "POST");

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


    protected ArrayList<Curriculum> getAllCurriculums(){

        String output = MainController.setConnection("curriculum", "GET");

        JsonReader reader = new JsonReader(new StringReader(output));
        reader.setLenient(true);
        int i = 1;
        ArrayList<Curriculum> curriculums;
        curriculums = gson.fromJson(reader, new TypeToken<List<Curriculum>>(){}.getType());

        return curriculums;
    }

    protected int extractCurriculumID(){
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
        System.out.println("Choose number on School:");
        choice = input.nextInt();
        choice--;
        int finalChoice = choice;
        curriculumArrayList.removeIf(Curriculum -> !Curriculum.getSchool().contains(strings.get(finalChoice))); //fra http://stackoverflow.com/questions/9146224/arraylist-filter
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
        System.out.println("Enter number on education: ");
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

        return curriculumID;
    }

    protected int getBooksFromCurriculum(){
       int i = 1;
        int curriculumID = extractCurriculumID();

        String s = "curriculum/"+curriculumID+"/books";


        String output = MainController.setConnection(s,"GET");

        JsonReader reader = new JsonReader(new StringReader(output));
        reader.setLenient(true);

        ArrayList<Book> books = gson.fromJson(reader, new TypeToken<List<Book>>(){}.getType());

        // Header i bogvisning
        System.out.printf("%-7s %-55s %-70s %-20s\n", "Nr.",  "Book title:", "Book Author", "Book ISBN", "Book Publisher: ");
        for(Book book : books){
            System.out.printf("%-7d %-55s %-70s %-20.0f\n", i,  book.getTitle(), book.getAuthor(), book.getISBN(), book.getPublisher());
            i++;
        }

        return curriculumID;

    }

    protected void getAllBooks(){
        int i = 1;
        String output = MainController.setConnection("book", "GET");
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













}
