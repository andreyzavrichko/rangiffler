package rangiffler.exception;

import java.util.NoSuchElementException;

/**
 * Исключение, которое выбрасывается, если пользователь не найден.
 */
public class UserNotFoundException extends NoSuchElementException {

  public UserNotFoundException(String userAttribute) {
    super("Пользователь " + userAttribute + " не найден");
  }
}
