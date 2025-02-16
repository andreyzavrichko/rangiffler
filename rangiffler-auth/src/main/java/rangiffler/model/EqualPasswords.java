package rangiffler.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import rangiffler.service.EqualPasswordsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки, что два пароля одинаковы.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EqualPasswordsValidator.class})
public @interface EqualPasswords {

  /**
   * Сообщение об ошибке, которое будет отображаться, если пароли не равны.
   *
   * @return сообщение об ошибке.
   */
  String message() default "Passwords should be equal";

  /**
   * Группы для группировки ограничений.
   *
   * @return массив групп.
   */
  Class<?>[] groups() default {};

  /**
   * Дополнительные данные о полезной нагрузке.
   *
   * @return массив полезных данных.
   */
  Class<? extends Payload>[] payload() default {};
}
