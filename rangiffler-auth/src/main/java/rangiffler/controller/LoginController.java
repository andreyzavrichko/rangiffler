package rangiffler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для обработки запросов на страницу логина.
 * <p>
 * Обрабатывает запросы для отображения страницы входа в систему.
 */
@Controller
public class LoginController {

  // Название представления для страницы логина
  private static final String LOGIN_VIEW_NAME = "login";

  /**
   * Обрабатывает GET-запросы на страницу логина.
   *
   * @return имя представления для страницы логина.
   */
  @GetMapping("/login")
  public String login() {
    return LOGIN_VIEW_NAME;  // Возвращает имя страницы логина
  }
}
