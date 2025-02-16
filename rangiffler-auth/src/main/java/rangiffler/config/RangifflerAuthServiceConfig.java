package rangiffler.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import rangiffler.config.keys.KeyManager;
import rangiffler.service.SpecificRequestDumperFilter;
import rangiffler.service.cors.CorsCustomizer;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * Конфигурация сервиса аутентификации Rangiffler, включая настройки безопасности,
 * OAuth2 и JWT авторизацию, а также клиентские настройки для OAuth2.
 */
@Configuration
public class RangifflerAuthServiceConfig {

  private final KeyManager keyManager;
  private final String rangifflerFrontUri;
  private final String rangifflerAuthUri;
  private final String clientId;
  private final CorsCustomizer corsCustomizer;
  private final String serverPort;
  private final String defaultHttpsPort = "443";
  private final Environment environment;

  @Autowired
  public RangifflerAuthServiceConfig(KeyManager keyManager,
                                     @Value("${rangiffler-front.base-uri}") String rangifflerFrontUri,
                                     @Value("${rangiffler-auth.base-uri}") String rangifflerAuthUri,
                                     @Value("${oauth2.client-id}") String clientId,
                                     @Value("${server.port}") String serverPort,
                                     CorsCustomizer corsCustomizer,
                                     Environment environment) {
    this.keyManager = keyManager;
    this.rangifflerFrontUri = rangifflerFrontUri;
    this.rangifflerAuthUri = rangifflerAuthUri;
    this.clientId = clientId;
    this.serverPort = serverPort;
    this.corsCustomizer = corsCustomizer;
    this.environment = environment;
  }

  /**
   * Конфигурация фильтра безопасности для сервера авторизации.
   * Включает OpenID Connect и настройки обработки ошибок.
   *
   * @param http - объект конфигурации безопасности.
   * @param entryPoint - точка входа для аутентификации.
   * @return конфигурированный фильтр безопасности.
   * @throws Exception если возникает ошибка при конфигурации.
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                    LoginUrlAuthenticationEntryPoint entryPoint) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    // Включение логирования запросов для локальных и staging профилей
    if (environment.acceptsProfiles(Profiles.of("local", "staging"))) {
      http.addFilterBefore(new SpecificRequestDumperFilter(
              new RequestDumperFilter(),
              "/login", "/oauth2/.*"
      ), DisableEncodeUrlFilter.class);
    }

    // Включение OpenID Connect 1.0
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());

    // Обработка ошибок аутентификации
    http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
            .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));

    // Конфигурация CORS
    corsCustomizer.corsCustomizer(http);
    return http.build();
  }

  /**
   * Конфигурация аутентификационной точки входа для HTTPS в продакшн и staging профилях.
   * Перенаправляет на HTTPS с соответствующими портами.
   *
   * @return конфигурированная точка входа для аутентификации.
   */
  @Bean
  @Profile({"staging", "prod"})
  public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttps() {
    LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/login");
    PortMapperImpl portMapper = new PortMapperImpl();
    portMapper.setPortMappings(Map.of(
            serverPort, defaultHttpsPort,
            "80", defaultHttpsPort,
            "8080", "8443"
    ));
    PortResolverImpl portResolver = new PortResolverImpl();
    portResolver.setPortMapper(portMapper);
    entryPoint.setForceHttps(true);
    entryPoint.setPortMapper(portMapper);
    entryPoint.setPortResolver(portResolver);
    return entryPoint;
  }

  /**
   * Конфигурация аутентификационной точки входа для HTTP в локальных и Docker профилях.
   *
   * @return конфигурированная точка входа для аутентификации.
   */
  @Bean
  @Profile({"local", "docker"})
  public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttp() {
    return new LoginUrlAuthenticationEntryPoint("/login");
  }

  /**
   * Регистрация клиента для OAuth2 с уникальным идентификатором и настройками.
   * @return объект RegisteredClientRepository, содержащий конфигурацию клиента.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientId)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(rangifflerFrontUri + "/authorized")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(true).build())
            .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(1))
                    .refreshTokenTimeToLive(Duration.ofHours(10))
                    .build())
            .build();

    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  /**
   * Создание PasswordEncoder для безопасной работы с паролями.
   * @return объект PasswordEncoder.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * Конфигурация настроек авторизационного сервера, включая издателя URI.
   * @return объект AuthorizationServerSettings с URI издателя.
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
            .issuer(rangifflerAuthUri)
            .build();
  }

  /**
   * Конфигурация источника JWK для декодирования JWT.
   * @return объект JWKSource для извлечения ключей.
   * @throws NoSuchAlgorithmException если не удается создать ключи.
   */
  @Bean
  public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
    JWKSet set = new JWKSet(keyManager.rsaKey());
    return (jwkSelector, securityContext) -> jwkSelector.select(set);
  }

  /**
   * Конфигурация декодера JWT с использованием JWKSource.
   * @param jwkSource источник JWK для декодирования.
   * @return объект JwtDecoder.
   */
  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }
}
