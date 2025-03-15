package rangiffler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import rangiffler.service.cors.CorsCustomizer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * SecurityConfigMain - класс конфигурации безопасности для основного (не локального) профиля.
 *
 * Эта конфигурация обеспечивает доступ к путям "/session" и "/actuator/health" без аутентификации,
 * в то время как все остальные запросы требуют аутентификации. Также настраивается поддержка
 * OAuth2 с использованием JWT для защиты ресурсов.
 */
@EnableWebSecurity
@Configuration
@Profile({"staging", "prod"})// Конфигурация применяется только в случае, если активен профиль, отличный от "local"
public class SecurityConfigMain {

  private final CorsCustomizer corsCustomizer;

  /**
   * Конструктор для инициализации SecurityConfigMain с заданным CorsCustomizer.
   *
   * @param corsCustomizer объект для настройки CORS
   */
  @Autowired
  public SecurityConfigMain(CorsCustomizer corsCustomizer) {
    this.corsCustomizer = corsCustomizer;
  }

  /**
   * Конфигурирует безопасность HTTP для основного профиля.
   * <ul>
   *   <li>Разрешает неаутентифицированный доступ к путям "/session" и "/actuator/health"</li>
   *   <li>Требует аутентификации для всех остальных запросов</li>
   *   <li>Настроен OAuth2 для сервера ресурсов с использованием JWT</li>
   * </ul>
   *
   * @param http объект HttpSecurity для конфигурации правил безопасности
   * @return SecurityFilterChain, который определяет правила безопасности для приложения
   * @throws Exception если возникает ошибка при конфигурации
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Применение пользовательской конфигурации CORS
    corsCustomizer.corsCustomizer(http);

    // Настройка безопасности
    http.authorizeHttpRequests(customizer ->
            customizer.requestMatchers(
                            antMatcher("/session"),
                            antMatcher("/actuator/health"))
                    .permitAll() // Разрешить доступ без аутентификации
                    .anyRequest()
                    .authenticated() // Требовать аутентификацию для всех остальных запросов
    ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // Настройка OAuth2 для использования JWT

    return http.build();
  }
}
