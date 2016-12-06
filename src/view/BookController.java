package view;

import sdk.model.CachedData;
import sdk.model.Book;
import sdk.model.Curriculum;
import sdk.connection.ResponseCallback;
import sdk.services.BookService;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by magnusrasmussen on 29/10/2016.
 */
public class BookController {

    private Scanner input;
    private BookService bookService;
    public static int curriculumID;
    public CachedData cachedData;

    public BookController() {
        this.input = new Scanner(System.in);
        this.bookService = new BookService();
        this.cachedData = new CachedData();
    }

    public void createNewCurriculum() {
        String institution = null, education = null;
        int semester = 0;
        boolean inputOk = false;


        do {
            try {
                System.out.println("You are about to create af new curriculum. Please enter name on institution:");
                institution = input.nextLine();
                System.out.println("Enter name on education:");
                education = input.nextLine();
                System.out.println("Enter which semester the curriculum belongs to:");
                semester = input.nextInt();
                inputOk = true;
            } catch (InputMismatchException e) {
                System.err.println("Seems like you entered a bad value! Please try again!");
                inputOk = false;
            }
            input.nextLine();
        } while (!inputOk);

        Curriculum curriculum = new Curriculum(institution, education, semester);

        bookService.createNewCurriculum(curriculum, new ResponseCallback<String>() {
            @Override
            public void success(String data) {
                System.out.println(data);
            }

            @Override
            public void error(int status) {
                System.err.println("Sorry, something went wrong! Error code: " + status);
            }
        });


    }

    public void createNewBook() {
        String publisher = null, title = null, author = null;
        double priceAB = 0, priceSAXO = 0, priceCDON = 0, ISBN = 0;
        int version = 0;
        boolean inputOk = false;

        System.out.println("First you need to select the corresponding curriculum the book belongs to:");
        getAllCurriculums();
        System.out.println("Great! Now enter the book information: ");

        do {
            try {
                System.out.println("Enter title: ");
                title = input.nextLine();
                System.out.println("Enter author: ");
                author = input.nextLine();
                System.out.println("Enter publisher: ");
                publisher = input.nextLine();
                System.out.println("Enter version: ");
                version = input.nextInt();
                System.out.println("Enter book ISBN:");
                ISBN = input.nextDouble();
                System.out.println("Enter price at Amazon: ");
                priceAB = input.nextDouble();
                System.out.println("Enter price at SAXO:");
                priceSAXO = input.nextDouble();
                System.out.println("Enter price at CDON:");
                priceCDON = input.nextDouble();
                inputOk = true;
            } catch (InputMismatchException e) {
                System.out.println("Seems like you entered a bad value! Please try again!");
                inputOk = false;
            }
            input.nextLine();
        } while (!inputOk);

        Book book = new Book(publisher, title, author, version, ISBN, priceAB, priceSAXO, priceCDON, curriculumID);

        bookService.createNewBook(book, new ResponseCallback<String>() {
            @Override
            public void success(String data) {
                System.out.println(data);
            }

            @Override
            public void error(int status) {
                System.err.println("Sorry, something went wrong! Error code: " + status);

            }
        });

    }

    protected void getAllCurriculums() {

        bookService.getAllCurriculums(new ResponseCallback<ArrayList<Curriculum>>() {
            public void success(ArrayList<Curriculum> curriculum) {
                extractCurriculumID(curriculum);
            }

            public void error(int status) {
                System.err.println(status);
            }
        });

    }

    /**
     * Lets the user extracts the curriculum id, by first showing school then removing all other schools. After that it shows the available educations
     * and removing all others, and lastly lets the user enter which semester he is attending, which will result in a unique id used to get alle books
     * from a specific curriculum
     * @param curriculumArrayList
     * @return
     */
    private int extractCurriculumID(ArrayList<Curriculum> curriculumArrayList) {

        int j = 1, choice;
        ArrayList<String> strings = new ArrayList(); // temp list used in for loops, to print every different entry only once.

        /*
        Loop that shows all the different schools only once.
         */
        for (Curriculum c : curriculumArrayList) {
            if (!strings.contains(c.getSchool())) {
                System.out.println("Number " + j + ":\t " + c.getSchool());
                strings.add(c.getSchool());
                j++;
            } else if (strings.size() == 0) {
                strings.add(c.getSchool());
            }
        }

        // Hereafter the user enters the number at which school he attends.
        do {
            System.out.println("Enter number on institution:");
            choice = input.nextInt();
        } while (choice > strings.size());
        int finalChoice = --choice;
        //And now removing all schools that doesn't match the school of the entered number
        curriculumArrayList.removeIf(Curriculum -> !Curriculum.getSchool().contains(strings.get(finalChoice))); //inspiration fra http://stackoverflow.com/questions/9146224/arraylist-filter
        strings.clear();

        j = 1;
        /*
        Loop that shows all educations only once and from the selected school.
         */
        for (Curriculum c : curriculumArrayList) {
            if (!strings.contains(c.getEducation())) {
                System.out.println("Number " + j + ":\t " + c.getEducation());
                strings.add(c.getEducation());
                j++;
            } else if (strings.size() == 0) {
                strings.add(c.getEducation());
            }
        }
        // Then the user enters which education he is attending.
        do {
            System.out.println("Enter number on education: ");
            choice = input.nextInt();
        } while (choice > strings.size());

        int finalChoice2 = --choice;
        //And alle other educations are removed.
        curriculumArrayList.removeIf(Curriculum -> !Curriculum.getEducation().contains(strings.get(finalChoice2)));

        boolean semesterFound = false;
        // Loop letting the user enter semester. If invalid semester is entered, a list of available semesters will be shown.
        do {
            System.out.println("\nEnter number on semester:");
            int semesterChoice = input.nextInt();
            for (int f = 0; f < curriculumArrayList.size(); f++) {
                if (curriculumArrayList.get(f).getSemester() == semesterChoice) {
                    curriculumID = curriculumArrayList.get(f).getCurriculumID(); //If the entered number matches a available semester, the curriculumid is found!
                    semesterFound = true;
                    break;
                }

            }
            if (!semesterFound) {
                System.err.println("\nSeems like you entered a semester that doesn't exists yet! Only the following semesters are available for your education:");
                for (Curriculum c : curriculumArrayList) {
                    System.out.print("\n" + c.getSemester());
                }
                System.err.println("\nPlease try again or contact your admin if you wish to have your semester on the list!");
            }
        } while (!semesterFound);
        input.nextLine();
        return curriculumID;
    }

    public void getBooksFromCurriculum() {
        getAllCurriculums();
        bookService.getBooksFromCurriculum(new ResponseCallback<ArrayList<Book>>() {
            public void success(ArrayList<Book> books) {
                boolean continueInput = false;
                int bookToGetInfo;

                do {
                    int i = 1;
                    // Header
                    System.out.printf("%-7s %-55s %-80s %-25s\n", "Nr.", "Book title:", "Book Author:", "Book ISBN:");
                    for (Book book : books) {
                        System.out.printf("%-7d %-55s %-80s %-25.0f\n", i, book.getTitle(), book.getAuthor(), book.getISBN());
                        i++;
                    }

                    System.out.println("Enter number on book you wish to retrieve price info: ");
                    bookToGetInfo = input.nextInt();
                    bookToGetInfo--;
                    System.out.printf("You have chosen: \n\"%s\" \nPrice at Academic Books: %8.2f kr. \nPrice at CDON: %10.2f kr. \nPrice at SAXO: %10.2f kr.\n", books.get(bookToGetInfo).getTitle(), books.get(bookToGetInfo).getPriceAB(), books.get(bookToGetInfo).getPriceCDON(), books.get(bookToGetInfo).getPriceSAXO());

                    System.out.println("Do you wish to get price info on another book? Press 1 for for yes. Press 2 for no");
                    if (input.nextInt() == 1) {
                        continueInput = true;
                    } else {
                        continueInput = false;
                    }
                } while (continueInput);
            }

            public void error(int status) {
                System.err.println("Sorry, an error occurred! Error code: " + status);
            }
        });
    }

    public void getAllBooks(CachedData cachedData) {
        bookService.getAll(cachedData, new ResponseCallback<ArrayList<Book>>() {
            public void success(ArrayList<Book> books) {
                int i = 1;
                // Header
                System.out.printf("%-7s %-55s %-80s %-25s %-25s %-25s %-25s\n", "Nr.", "Book title:", "Book Author:", "Book ISBN:", "Book Price Academic Books:", "Book Price SAXO:", "Book Price CDON:");
                for (Book book : books) {
                    System.out.printf("%-7d %-55s %-80s %-25.0f %-25.2f %-25.2f %-25.2f\n", i, book.getTitle(), book.getAuthor(), book.getISBN(), book.getPriceAB(), book.getPriceSAXO(), book.getPriceCDON());
                    i++;
                }
            }

            public void error(int status) {
                System.err.println("Sorry, an error occurred! Error code: " + status);
            }
        });
    }
}
