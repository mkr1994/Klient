package sdk.Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import sdk.Model.Book;
import sdk.Model.Curriculum;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


    protected ArrayList<Curriculum> getAllCurriculums(){

        String output = MainController.setConnection("curriculum", "GET");

        JsonReader reader = new JsonReader(new StringReader(output));
        reader.setLenient(true);
        int i = 1;
        ArrayList<Curriculum> curriculums;
        curriculums = gson.fromJson(reader, new TypeToken<List<Curriculum>>(){}.getType());

        return curriculums;
    }

    protected void getBooksFromCurriculum(){
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
