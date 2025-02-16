package rangiffler.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import rangiffler.model.UserJson;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Конфигурация Kafka Producer для аутентификации пользователей в системе Rangiffler.
 * Создает необходимые компоненты для отправки сообщений в Kafka с использованием JSON сериализации.
 */
@Configuration
public class RangifflerAuthProducerConfiguration {

  private final KafkaProperties kafkaProperties;

  /**
   * Конструктор для инъекции KafkaProperties.
   * @param kafkaProperties свойства Kafka, полученные из конфигурации Spring Boot.
   */
  @Autowired
  public RangifflerAuthProducerConfiguration(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }

  /**
   * Бин, конфигурирующий настройки для Kafka Producer.
   * @return Map с конфигурацией для Producer, включая сериализаторы для ключа и значения.
   */
  @Bean
  public Map<String, Object> producerConfiguration() {
    Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties(
            new DefaultSslBundleRegistry() // Используется для SSL конфигурации, если необходимо.
    ));
    // Настройка сериализаторов для ключа и значения
    properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return properties;
  }

  /**
   * Бин для создания ProducerFactory с конфигурацией для отправки сообщений в Kafka.
   * @return ProducerFactory для сериализации строковых ключей и объектов типа UserJson.
   */
  @Bean
  public ProducerFactory<String, UserJson> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfiguration());
  }

  /**
   * Бин для KafkaTemplate, который используется для отправки сообщений в Kafka.
   * @return KafkaTemplate для отправки сообщений с ключом типа String и значением типа UserJson.
   */
  @Bean
  public KafkaTemplate<String, UserJson> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  /**
   * Бин для создания Kafka топика с именем "users", 10 партициями и 1 репликой.
   * @return Новый топик Kafka, используемый для отправки данных о пользователях.
   */
  @Bean
  @Primary // Обозначает основной бин для контекста, если существует несколько топиков.
  public NewTopic topic() {
    return TopicBuilder.name("users")
            .partitions(10) // Количество партиций в топике
            .replicas(1)     // Количество реплик
            .build();
  }
}
