package sdk.connection;

public interface ResponseCallback<T> {

  void success(T data);
  void error(int status);

}
