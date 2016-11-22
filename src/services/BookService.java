package services;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import sdk.Controller.BookController;
import sdk.Controller.MainController;
import sdk.Encrypters.Crypter;
import sdk.Model.Curriculum;
import sdk.connection.Connection;
import sdk.connection.ResponseCallback;
import sdk.connection.ResponseParser;
import sdk.Model.Book;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by magnusrasmussen on 22/11/2016.
 */
public class BookService {

    private Connection connection;
    private Gson gson;

    public BookService() {
        this.connection = new Connection();
        this.gson = new Gson();
    }

    public void create(Book book, final ResponseCallback<String> responseCallback) {
        HttpPost postRequest = new HttpPost(Connection.serverURL + "book");
        try {
            StringEntity bookString = new StringEntity(Crypter.encryptDecryptXOR(this.gson.toJson(book)));
            postRequest.setEntity(bookString);
            postRequest.setHeader("authorization", MainController.token);
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
            e.printStackTrace();
        }
    }

    public void getBooksFromCurriculum(final ResponseCallback<ArrayList<Book>> responseCallback){
        HttpGet getRequest = new HttpGet(Connection.serverURL + "curriculum/"+ BookController.curriculumID+"/books");

        this.connection.execute(getRequest, new ResponseParser() {
            public void payload(String json) {
                ArrayList<Book> books = gson.fromJson(Crypter.encryptDecryptXOR(json), new TypeToken<ArrayList<Book>>() {
                }.getType());
                responseCallback.success(books);
            }

            public void error(int status) {

                responseCallback.error(status);
            }
        });
    }


    public void getAll(final ResponseCallback<ArrayList<Book>> responseCallback){
        HttpGet getRequest = new HttpGet(Connection.serverURL + "book");

        this.connection.execute(getRequest, new ResponseParser() {
            public void payload(String json) {
                ArrayList<Book> books = gson.fromJson(Crypter.encryptDecryptXOR(json), new TypeToken<ArrayList<Book>>() {
                }.getType());
                responseCallback.success(books);
            }

            public void error(int status) {

                responseCallback.error(status);
            }
        });
    }

    public void getAllCurriculums(final ResponseCallback<ArrayList<Curriculum>> responseCallback){
        HttpGet getRequest = new HttpGet(Connection.serverURL + "curriculum");

        this.connection.execute(getRequest, new ResponseParser() {
            public void payload(String json) {
                ArrayList<Curriculum> curriculums = gson.fromJson(Crypter.encryptDecryptXOR(json), new TypeToken<ArrayList<Curriculum>>() {
                }.getType());
                responseCallback.success(curriculums);
            }

            public void error(int status) {

                responseCallback.error(status);
            }
        });
    }


}
