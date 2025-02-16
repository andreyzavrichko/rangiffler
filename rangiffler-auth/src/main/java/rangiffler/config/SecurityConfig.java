package rangiffler.config;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import rangiffler.service.SpecificRequestDumperFilter;
import rangiffler.service.cors.CookieCsrfFilter;
import rangiffler.service.cors.CorsCustomizer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Конфигурация безопасности приложения.
 * <p>
 * Настройка фильтров безопасности, обработки CSRF, CORS и аутентификации/авторизации.
 */
@Configuration
public class SecurityConfig {

    private final CorsCustomizer corsCustomizer;
    private final Environment environment;

    /**
     * Конструктор конфигурации безопасности.
     *
     * @param corsCustomizer объект для настройки CORS.
     * @param environment    объект, предоставляющий доступ к настройкам среды.
     */
    @Autowired
    public SecurityConfig(CorsCustomizer corsCustomizer, Environment environment) {
        this.corsCustomizer = corsCustomizer;
        this.environment = environment;
    }

    /**
     * Основная конфигурация безопасности, настраивающая фильтры и защиту.
     *
     * @param http объект конфигурации безопасности HTTP.
     * @return {@link SecurityFilterChain} объект с конфигурацией безопасности.
     * @throws Exception исключения при настройке безопасности.
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Настройка CORS
        corsCustomizer.corsCustomizer(http);

        // Добавление фильтра для логирования запросов в режиме разработки и staging
        if (environment.acceptsProfiles(Profiles.of("local", "staging"))) {
            http.addFilterBefore(new SpecificRequestDumperFilter(
                    new RequestDumperFilter(),
                    "/login", "/oauth2/.*"
            ), DisableEncodeUrlFilter.class);
        }

        // Основная настройка безопасности HTTP
        return http.authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(
                                antMatcher("/register"),
                                antMatcher("/images/**"),
                                antMatcher("/styles/**"),
                                antMatcher("/fonts/**"),
                                antMatcher("/actuator/health")
                        ).permitAll()  // Разрешить доступ без аутентификации
                        .anyRequest()
                        .authenticated()  // Все остальные запросы требуют аутентификации
                )
                // Настройка защиты от CSRF
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Использование токенов CSRF в cookie
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())  // Обработчик CSRF токенов
                )
                .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)  // Фильтр для обработки CSRF
                // Настройка страницы логина
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll())  // Разрешить доступ к странице логина
                // Настройка выхода из системы
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                antMatcher("/logout"))  // Настройка маршрута для выхода
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")  // Удалить cookies после выхода
                        .invalidateHttpSession(true)  // Инвалидировать сессию
                        .clearAuthentication(true)  // Очистить аутентификацию
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))  // Обработчик успешного выхода
                )
                // Настройка управления сессиями
                .sessionManagement(sm -> sm.invalidSessionUrl("/login"))  // Настройка страницы для недействительной сессии
                .build();
    }
}
