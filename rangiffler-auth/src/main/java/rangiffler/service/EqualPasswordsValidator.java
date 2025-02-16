package rangiffler.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import rangiffler.model.EqualPasswords;
import rangiffler.model.RegistrationModel;

/**
 * Валидатор, проверяющий, что пароли совпадают в процессе регистрации.
 * Реализует интерфейс {@link ConstraintValidator} для выполнения кастомной валидации.
 */
public class EqualPasswordsValidator implements ConstraintValidator<EqualPasswords, RegistrationModel> {

  /**
   * Метод для выполнения проверки на совпадение паролей.
   * Проверяет, что введенные пароли совпадают.
   *
   * @param form объект модели регистрации, содержащий пароли.
   * @param context контекст валидации, который используется для добавления ошибок.
   * @return true, если пароли совпадают, иначе false.
   */
  @Override
  public boolean isValid(RegistrationModel form, ConstraintValidatorContext context) {
    // Проверка совпадения паролей
    boolean isValid = form.password().equals(form.passwordSubmit());

    // Если пароли не совпадают, добавляем ошибку в контекст
    if (!isValid) {
      context.disableDefaultConstraintViolation(); // Отключаем стандартное сообщение об ошибке
      context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()) // Используем дефолтное сообщение
              .addPropertyNode("password") // Ошибка привязывается к полю password
              .addConstraintViolation(); // Добавляем саму ошибку в контекст
    }
    return isValid; // Возвращаем результат проверки
  }
}
