package rangiffler.service.cors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorsCustomizerTest {

  private final CorsCustomizer corsCustomizer = new CorsCustomizer("front-uri", "auth-uri");

  @Test
  void shouldApplyCorsCustomizer(@Mock HttpSecurity httpSecurity) throws Exception {
    // Act
    corsCustomizer.corsCustomizer(httpSecurity);

    // Assert
    verify(httpSecurity, times(1)).cors(any(Customizer.class));
  }

  @Test
  void shouldReturnCorsConfigurationSource() {
    // Act
    CorsConfigurationSource configurationSource = corsCustomizer.corsConfigurationSource();
    CorsConfiguration configuration = configurationSource.getCorsConfiguration(null);

    // Assert
    Assertions.assertNotNull(configuration);
    Assertions.assertTrue(configuration.getAllowCredentials(), "Credentials should be allowed");
    Assertions.assertEquals(List.of("front-uri", "auth-uri"), configuration.getAllowedOrigins(), "Allowed origins mismatch");
    Assertions.assertEquals(List.of("*"), configuration.getAllowedHeaders(), "Allowed headers mismatch");
    Assertions.assertEquals(List.of("*"), configuration.getAllowedMethods(), "Allowed methods mismatch");
  }

  @Test
  void shouldCustomizeCorsConfiguration(@Mock CorsConfigurer<HttpSecurity> corsConfigurer) {
    // Act
    Customizer<CorsConfigurer<HttpSecurity>> customizer = corsCustomizer.customizer();
    customizer.customize(corsConfigurer);

    // Assert
    verify(corsConfigurer, times(1)).configurationSource(any(CorsConfigurationSource.class));
  }
}
