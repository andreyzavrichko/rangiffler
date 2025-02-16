package rangiffler.exception;

import java.util.NoSuchElementException;

public class PhotoNotFoundException extends NoSuchElementException {

  public PhotoNotFoundException(String photoId) {
    super("Фото с идентификатором " + photoId + " не найдено");
  }
}
