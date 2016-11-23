package sdk.connection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.cache.CachingHttpClient;
import sdk.encrypters.Crypter;
import sdk.model.Book;


import java.io.IOException;
import java.util.ArrayList;

public class Connection {

    public static String serverURL = "http://localhost:8080/Server_war_exploded/";
    private CloseableHttpClient httpClient;
    public CachedData cachedData = new CachedData();

    public Connection() {
        this.httpClient = HttpClients.createDefault();
    }


    public void execute(HttpUriRequest uriRequest, final ResponseParser methods) {

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    methods.error(status);
                }
                return null;
            }

        };

        try {
            String json = this.httpClient.execute(uriRequest, responseHandler);

            if (json != null) {
                // System.out.println(cachedData.getBookArrayList().get(1).getTitle());
                cachedData.setString(json);
                methods.payload(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
