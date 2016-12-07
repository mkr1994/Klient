package sdk.model;

import sdk.model.Book;

import java.util.ArrayList;

/**
 * This class contains to arraylists which can be used to store cached data
 * Created by magnusrasmussen on 23/11/2016.
 */
public class CachedData {

    public ArrayList<Book> bookArrayList = new ArrayList<>();
    public ArrayList<User> userArrayList = new ArrayList<>();

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    public ArrayList<Book> getBookArrayList() {
        return bookArrayList;
    }

    public void setBookArrayList(ArrayList<Book> bookArrayList) {
        this.bookArrayList = bookArrayList;
    }

    /**
     * Clear the ararylists
     */
    public void clearCache(){
        bookArrayList.clear();
        userArrayList.clear();
    }
}
