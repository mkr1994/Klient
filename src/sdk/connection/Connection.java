package sdk.connection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Connection {

  public static String serverURL = "http://localhost:8080/Server_war_exploded/";
  private CloseableHttpClient httpClient;


  public Connection(){
    this.httpClient = HttpClients.createDefault();
  }


  public void execute(HttpUriRequest uriRequest, final ResponseParser methods){

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
      if(json != null) {
        methods.payload(json);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
