package sdk.connection;

/**
 * Interfaces for handling success and error responses
 * @param <T>
 */
public interface ResponseCallback<T> {

  void success(T data);
  void error(int status);

}
