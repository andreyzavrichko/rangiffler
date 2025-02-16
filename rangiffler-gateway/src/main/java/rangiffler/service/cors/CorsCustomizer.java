package rangiffler.service.cors;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Компонент для настройки CORS (Cross-Origin Resource Sharing) для приложений Spring Security.
 * Этот класс настраивает разрешенные источники, заголовки и методы для CORS запросов.
 */
@Component
public class CorsCustomizer {

  private final String rangifflerFrontUri;

  /**
   * Конструктор, который инициализирует компонент с URI для фронтенда.
   *
   * @param rangifflerFrontUri URI фронтенда, который будет разрешен для CORS запросов
   */
  @Autowired
  public CorsCustomizer(@Value("${rangiffler-front.base-uri}") String rangifflerFrontUri) {
    this.rangifflerFrontUri = rangifflerFrontUri;
  }

  /**
   * Настроить CORS для HTTP Security.
   *
   * @param http объект HttpSecurity, с помощью которого настраиваются параметры безопасности для HTTP
   * @throws Exception если при настройке возникнут проблемы
   */
  public void corsCustomizer(@Nonnull HttpSecurity http) throws Exception {
    http.cors(c -> {
      CorsConfigurationSource source = s -> {
        CorsConfiguration cc = new CorsConfiguration();

        // Разрешение на использование cookies при кросс-доменных запросах
        cc.setAllowCredentials(true);

        // Устанавливаем разрешенные источники (в данном случае, только указанный URI)
        cc.setAllowedOrigins(List.of(rangifflerFrontUri));

        // Разрешаем все заголовки
        cc.setAllowedHeaders(List.of("*"));

        // Разрешаем все HTTP методы (GET, POST и т.д.)
        cc.setAllowedMethods(List.of("*"));

        return cc;
      };

      // Применяем конфигурацию CORS
      c.configurationSource(source);
    });
  }
}
