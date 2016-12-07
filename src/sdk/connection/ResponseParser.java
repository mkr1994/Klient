package sdk.connection;

/**
 * Interface used for returning the servercalls payload
 */
public interface ResponseParser {

  void payload(String json);
  void error(int status);

}
