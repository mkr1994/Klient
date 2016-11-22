package sdk.connection;

import sdk.Model.Curriculum;

import java.util.ArrayList;

public interface ResponseCallback<T> {

  void success(T data);
  void error(int status);

}
