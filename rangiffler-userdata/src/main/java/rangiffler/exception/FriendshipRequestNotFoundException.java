package rangiffler.exception;

import java.util.NoSuchElementException;

/**
 * Исключение, которое выбрасывается, если запрос дружбы не найден.
 */
public class FriendshipRequestNotFoundException extends NoSuchElementException {

  public FriendshipRequestNotFoundException(String requester, String addressee) {
    super("Запрос дружбы от " + requester + " к " + addressee + " не найден");
  }
}
