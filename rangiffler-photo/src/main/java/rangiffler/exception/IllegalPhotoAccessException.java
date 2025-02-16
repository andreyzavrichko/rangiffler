package rangiffler.exception;

/**
 * Исключение, выбрасываемое, когда фото не может быть модифицировано пользователем.
 */
public class IllegalPhotoAccessException extends IllegalStateException {

  // Константа для сообщения об ошибке
  private static final String ERROR_MESSAGE = "Фото с идентификатором %s не может быть модифицирован пользователем %s";

  /**
   * Конструктор для создания исключения с указанием идентификатора фото и пользователя.
   *
   * @param photoId Идентификатор фото
   * @param userId Идентификатор пользователя
   */
  public IllegalPhotoAccessException(String photoId, String userId) {
    super(String.format(ERROR_MESSAGE, photoId, userId));
  }
}
