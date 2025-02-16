package rangiffler.exception;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Исключение, выбрасываемое, когда статистика для заданного пользователя и страны не найдена.
 */
public class StatisticNotFoundException extends NoSuchElementException {

  // Константа для сообщения об ошибке
  private static final String ERROR_MESSAGE = "Статистика для пользователя %s и идентификатором страны %s не найдена";

  /**
   * Конструктор для создания исключения с указанием идентификаторов пользователя и страны.
   *
   * @param userId Идентификатор пользователя
   * @param countryId Идентификатор страны
   */
  public StatisticNotFoundException(UUID userId, UUID countryId) {
    super(String.format(ERROR_MESSAGE, userId, countryId));
  }
}
