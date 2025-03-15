package rangiffler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import rangiffler.service.cors.CorsCustomizer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * SecurityConfigLocal - класс конфигурации безопасности для приложения в локальной среде.
 *
 * Эта конфигурация позволяет открытый доступ к определённым путям, таким как
 * "/session", "/actuator/health" и GraphQL-эндпоинты. Все остальные запросы требуют аутентификации.
 * Также, конфигурация включает поддержку OAuth2 для сервера ресурсов с использованием JWT.
 */
@EnableWebSecurity
@Configuration
@Profile({"local", "docker"})
public class SecurityConfigLocal {

  private final CorsCustomizer corsCustomizer;

  /**
   * Конструктор для инициализации SecurityConfigLocal с заданным CorsCustomizer.
   *
   * @param corsCustomizer объект для настройки CORS
   */
  @Autowired
  public SecurityConfigLocal(CorsCustomizer corsCustomizer) {
    this.corsCustomizer = corsCustomizer;
  }

  /**
   * Конфигурирует безопасность HTTP для локальной среды.
   * <ul>
   *   <li>Отключает защиту от CSRF</li>
   *   <li>Разрешает неаутентифицированный доступ к определённым путям, таким как /session, /actuator/health,
   *       GraphQL-эндпоинты и favicon.ico</li>
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
    http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(customizer ->
                    customizer.requestMatchers(
                                    antMatcher("/session"),
                                    antMatcher("/actuator/health"),
                                    antMatcher("/graphiql/**"),
                                    antMatcher("/graphql/**"),
                                    antMatcher("/favicon.ico"),
                                    antMatcher("/graphql"),
                                    antMatcher(HttpMethod.POST, "/graphql")
                            ).permitAll()
                            .anyRequest()
                            .authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }
}
