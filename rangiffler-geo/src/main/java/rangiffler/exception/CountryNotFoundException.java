package rangiffler.exception;

import java.util.NoSuchElementException;

/**
 * Исключение, выбрасываемое в случае, когда страна не найдена.
 * Расширяет стандартное исключение {@link NoSuchElementException}.
 */
public class CountryNotFoundException extends NoSuchElementException {

  /**
   * Конструктор, создающий исключение с сообщением о том, что страна не найдена.
   *
   * @param countryAttribute атрибут страны, который не был найден.
   */
  public CountryNotFoundException(String countryAttribute) {
    super("Страна " + countryAttribute + " не найдена");
  }
}
