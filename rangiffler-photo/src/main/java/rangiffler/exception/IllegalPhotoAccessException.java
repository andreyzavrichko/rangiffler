package rangiffler.exception;

public class IllegalPhotoAccessException extends IllegalStateException {

  public IllegalPhotoAccessException(String photoId, String userId) {
    super("Фото с идентификатором " + photoId + " не может быть модифицирован пользователем " + userId);
  }
}
