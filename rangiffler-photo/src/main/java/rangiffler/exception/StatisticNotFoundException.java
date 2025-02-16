package rangiffler.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

public class StatisticNotFoundException extends NoSuchElementException {

  public StatisticNotFoundException(UUID userId, UUID countryId) {
    super("Статистика для пользователя " + userId + " и идентификатором страны " + countryId + " не найдена");
  }
}
