package rangiffler.exception;

import java.util.NoSuchElementException;

public class CountryNotFoundException extends NoSuchElementException {

  public CountryNotFoundException(String countryAttribute) {
    super("Страна  " + countryAttribute + " не найдена");
  }
}
