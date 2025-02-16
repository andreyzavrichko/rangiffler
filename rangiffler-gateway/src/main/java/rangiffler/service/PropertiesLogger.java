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
 * Слушатель событий Spring, который логирует все свойства конфигурации,
 * загруженные в приложении, после того как контекст приложения был подготовлен.
 */
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {

  private static final Logger log = LoggerFactory.getLogger(PropertiesLogger.class);
  private ConfigurableEnvironment environment;
  private boolean isFirstRun = true;

  /**
   * Обрабатывает событие подготовки приложения. При первом запуске
   * инициализирует окружение и вызывает логирование всех свойств.
   *
   * @param event событие подготовки приложения
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
   * Логирует все свойства конфигурации из различных источников.
   * Для каждого свойства выводится его значение и информация о том,
   * было ли оно переопределено.
   */
  public void printProperties() {
    // Ищем все источники свойств
    for (EnumerablePropertySource<?> propertySource : findPropertiesPropertySources()) {
      log.info("******* {} *******", propertySource.getName());  // Логируем название источника
      String[] propertyNames = propertySource.getPropertyNames();
      Arrays.sort(propertyNames);  // Сортируем свойства для более удобного вывода
      for (String propertyName : propertyNames) {
        String resolvedProperty = environment.getProperty(propertyName);  // Получаем разрешенное значение свойства
        String sourceProperty = Objects.requireNonNull(propertySource.getProperty(propertyName)).toString();  // Получаем исходное значение свойства
        // Логируем, если свойство не было переопределено
        if (Objects.equals(resolvedProperty, sourceProperty)) {
          log.info("{}={}", propertyName, resolvedProperty);
        } else {
          // Логируем переопределение свойства
          log.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
        }
      }
    }
  }

  /**
   * Находит все источники свойств, которые могут быть перечислены (EnumerablePropertySource).
   *
   * @return список источников свойств
   */
  private List<EnumerablePropertySource<?>> findPropertiesPropertySources() {
    List<EnumerablePropertySource<?>> propertiesPropertySources = new LinkedList<>();
    // Проходим по всем источникам свойств
    for (PropertySource<?> propertySource : environment.getPropertySources()) {
      if (propertySource instanceof EnumerablePropertySource) {
        propertiesPropertySources.add((EnumerablePropertySource<?>) propertySource);
      }
    }
    return propertiesPropertySources;
  }
}
