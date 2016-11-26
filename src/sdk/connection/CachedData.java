package sdk.connection;

import sdk.model.Book;

import java.util.ArrayList;

/**
 * Created by magnusrasmussen on 23/11/2016.
 */
public class CachedData {

    String string;
    public ArrayList<Book> bookArrayList = new ArrayList<>();


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public ArrayList<Book> getBookArrayList() {
        return bookArrayList;
    }

    public void setBookArrayList(ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
    }

    public void clearCache(){
        bookArrayList.clear();
    }
}
