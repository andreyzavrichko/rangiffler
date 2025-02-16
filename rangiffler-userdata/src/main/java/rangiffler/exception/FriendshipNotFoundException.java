package rangiffler.exception;

import java.util.NoSuchElementException;

/**
 * Исключение, которое выбрасывается, если запрос дружбы не найден.
 */
public class FriendshipNotFoundException extends NoSuchElementException {

  public FriendshipNotFoundException(String actionAuthorUsername, String actionTargetUserName) {
    super("Дружба между " + actionAuthorUsername + " и " + actionTargetUserName + " не найдена");
  }
}
