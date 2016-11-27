package sdk.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import controller.MainController;
import sdk.encrypters.Crypter;
import sdk.model.CachedData;
import sdk.model.User;
import sdk.connection.Connection;
import sdk.connection.ResponseCallback;
import sdk.connection.ResponseParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by magnusrasmussen on 22/11/2016.
 */
public class UserService {
    private Connection connection;
    private Gson gson;

    public UserService() {
        this.connection = new Connection();
        this.gson = new Gson();
    }

    public void editUser(String path, User user, final ResponseCallback<String> responseCallback) {
        HttpPut putRequest = new HttpPut(Connection.serverURL + path);

        try {
            StringEntity userString = new StringEntity(Crypter.encryptDecryptXOR(this.gson.toJson(user)));
            putRequest.setEntity(userString);
            putRequest.setHeader("authorization", MainController.token);
            putRequest.setHeader("Content-Type", "application/json");

            this.connection.execute(putRequest, new ResponseParser() {
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

    public void deleteUser(String s, final ResponseCallback<Boolean> responseCallback) {
        HttpDelete deleteRequest = new HttpDelete(Connection.serverURL + s);
        try {
            deleteRequest.setHeader("authorization", MainController.token);
            deleteRequest.setHeader("Content-Type", "application/json");

            this.connection.execute(deleteRequest, new ResponseParser() {
                public void payload(String json) {
                    boolean bool = false;
                    if (json != null) {
                        bool = true;
                    }
                    responseCallback.success(bool);
                }

                public void error(int status) {
                    responseCallback.error(status);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserFromToken(final ResponseCallback<User> responseCallback) {
        HttpGet getRequest = new HttpGet(Connection.serverURL + "user/fromToken");

        getRequest.setHeader("authorization", MainController.token);
        getRequest.setHeader("Content-Type", "application/json");
        this.connection.execute(getRequest, new ResponseParser() {
            public void payload(String json) {
                User user = gson.fromJson(Crypter.encryptDecryptXOR(json), new TypeToken<User>() {
                }.getType());
                responseCallback.success(user);
            }

            public void error(int status) {

                responseCallback.error(status);
            }
        });
    }

    public void create(User user, final ResponseCallback<String> responseCallback) {
        HttpPost postRequest = new HttpPost(Connection.serverURL + "user");
        try {
            StringEntity userString = new StringEntity(Crypter.encryptDecryptXOR(this.gson.toJson(user)));
            postRequest.setEntity(userString);
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

    public void getAll(CachedData cachedData, final ResponseCallback<ArrayList<User>> responseCallback) {
        HttpGet getRequest = new HttpGet(Connection.serverURL + "user");

        if (!cachedData.getUserArrayList().isEmpty() && (System.currentTimeMillis() - MainController.startTime) < 60000) {
            responseCallback.success(cachedData.getUserArrayList());
        } else {

            getRequest.setHeader("authorization", MainController.token);
            getRequest.setHeader("Content-Type", "application/json");
            this.connection.execute(getRequest, new ResponseParser() {
                public void payload(String json) {
                    MainController.startTime = System.currentTimeMillis();
                    ArrayList<User> users = gson.fromJson(Crypter.encryptDecryptXOR(json), new TypeToken<ArrayList<User>>() {
                    }.getType());
                    responseCallback.success(users);
                }

                public void error(int status) {

                    responseCallback.error(status);
                }
            });
        }
    }


}
