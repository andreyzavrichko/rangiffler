package rangiffler.service;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Слушатель событий приложения, который логирует все свойства конфигурации,
 * доступные в окружении Spring после подготовки приложения.
 */
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {

  private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);

  private ConfigurableEnvironment environment;
  private boolean isFirstRun = true;

  /**
   * Этот метод вызывается после подготовки контекста приложения.
   * Он выполняет логирование всех конфигурационных свойств.
   *
   * @param event событие, возникающее при подготовке контекста приложения.
   */
  @Override
  public void onApplicationEvent(@Nonnull ApplicationPreparedEvent event) {
    if (isFirstRun) {
      environment = event.getApplicationContext().getEnvironment();
      printProperties();
    }
    isFirstRun = false;
  }

  /**
   * Логирует все свойства конфигурации, доступные в окружении приложения.
   * Для каждого источника свойств отображаются значения, а если свойство было переопределено,
   * это также фиксируется в логе.
   */
  public void printProperties() {
    for (EnumerablePropertySource<?> propertySource : findPropertiesPropertySources()) {
      log.info("******* {} *******", propertySource.getName());
      String[] propertyNames = propertySource.getPropertyNames();
      Arrays.sort(propertyNames);  // Сортировка для логирования свойств в алфавитном порядке
      for (String propertyName : propertyNames) {
        String resolvedProperty = environment.getProperty(propertyName);
        String sourceProperty = Objects.requireNonNull(propertySource.getProperty(propertyName)).toString();
        if (Objects.equals(resolvedProperty, sourceProperty)) {
          log.info("{}={}", propertyName, resolvedProperty);
        } else {
          log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
        }
      }
    }
  }

  /**
   * Находит все источники свойств, которые поддерживают перечисление (Enumerable).
   *
   * @return список источников свойств, которые могут быть перечислены.
   */
  private List<EnumerablePropertySource<?>> findPropertiesPropertySources() {
    List<EnumerablePropertySource<?>> propertiesPropertySources = new LinkedList<>();
    for (PropertySource<?> propertySource : environment.getPropertySources()) {
      if (propertySource instanceof EnumerablePropertySource) {
        propertiesPropertySources.add((EnumerablePropertySource<?>) propertySource);
      }
    }
    return propertiesPropertySources;
  }
}
