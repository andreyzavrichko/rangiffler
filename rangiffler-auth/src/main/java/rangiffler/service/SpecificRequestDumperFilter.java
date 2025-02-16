package rangiffler.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Фильтр, который обрабатывает определенные URL-шаблоны и выполняет дополнительную обработку
 * запроса через переданный фильтр {@link GenericFilter}, если URL соответствует одному из шаблонов.
 */
public class SpecificRequestDumperFilter extends GenericFilter implements Filter, FilterConfig, Serializable {

  private final String[] urlPatterns;
  private final GenericFilter filterToDecorate;

  /**
   * Конструктор фильтра.
   *
   * @param filterToDecorate фильтр, который будет обработан, если URI соответствует одному из шаблонов.
   * @param urlPatterns массив строковых шаблонов для сравнения с URI запроса.
   */
  public SpecificRequestDumperFilter(GenericFilter filterToDecorate, String... urlPatterns) {
    this.filterToDecorate = filterToDecorate;
    this.urlPatterns = Arrays.copyOf(urlPatterns, urlPatterns.length);
  }

  /**
   * Выполняет фильтрацию запроса. Если URI запроса соответствует одному из указанных шаблонов,
   * применяется дополнительная обработка через {@link GenericFilter}.
   * В противном случае запрос передается дальше по цепочке фильтров.
   *
   * @param request запрос, полученный фильтром.
   * @param response ответ, генерируемый фильтром.
   * @param chain цепочка фильтров для продолжения обработки запроса.
   * @throws IOException если возникает ошибка ввода-вывода при обработке запроса.
   * @throws ServletException если возникает ошибка в процессе выполнения фильтра.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
    if (request instanceof HttpServletRequest hRequest) {
      // Проверка на совпадение с шаблонами URL
      for (String urlPattern : urlPatterns) {
        if (hRequest.getRequestURI().matches(urlPattern)) {
          // Выполнение декорированного фильтра, если URL совпадает с шаблоном
          filterToDecorate.doFilter(request, response, chain);
          return;
        }
      }
    }
    // Передача запроса дальше, если шаблон не совпал
    chain.doFilter(request, response);
  }

  /**
   * Освобождает ресурсы фильтра. Вызывается при уничтожении фильтра.
   */
  @Override
  public void destroy() {
    filterToDecorate.destroy();
  }
}
