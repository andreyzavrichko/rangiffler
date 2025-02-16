package rangiffler.exception;

import java.util.NoSuchElementException;

/**
 * Исключение, выбрасываемое, когда фото с заданным идентификатором не найдено.
 */
public class PhotoNotFoundException extends NoSuchElementException {

  // Константа для сообщения об ошибке
  private static final String ERROR_MESSAGE = "Фото с идентификатором %s не найдено";

  /**
   * Конструктор для создания исключения с указанием идентификатора фото.
   *
   * @param photoId Идентификатор фото
   */
  public PhotoNotFoundException(String photoId) {
    super(String.format(ERROR_MESSAGE, photoId));
  }
}
