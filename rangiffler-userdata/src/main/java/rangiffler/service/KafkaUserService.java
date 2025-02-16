package rangiffler.service;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rangiffler.data.UserEntity;
import rangiffler.data.repository.UserRepository;
import rangiffler.grpc.Country;
import rangiffler.grpc.RangifflerGeoServiceGrpc;
import rangiffler.model.UserJson;

import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaUserService {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaUserService.class);
  private final UserRepository userRepository;
  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;

  private final List<Country> cachedCountries = new ArrayList<>();

  @Autowired
  public KafkaUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    LOG.info("### Kafka topic [users] received message: {}", user);
    LOG.info("### Kafka consumer record: {}", cr);

    try {
      // Получение данных о странах (используется кэширование)
      List<Country> countries = getCountries();

      // Найти страну по коду
      var userCountry = countries.stream()
              .filter(s -> s.getCode().equalsIgnoreCase(user.countryCode()))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("Country not found for code: " + user.countryCode()));

      // Создание и сохранение пользователя
      var userEntity = new UserEntity();
      userEntity.setUsername(user.username());
      userEntity.setCountryId(userCountry.getId());
      userRepository.save(userEntity);

      LOG.info("### User {} successfully saved to database with country id {}", user.username(), userEntity.getCountryId());
    } catch (Exception e) {
      LOG.error("Error processing user {}: {}", user.username(), e.getMessage(), e);
      // Возможно, стоит отправить сообщение в отдельный топик для отладки
    }
  }

  private List<Country> getCountries() {
    if (cachedCountries.isEmpty()) {
      cachedCountries.addAll(rangifflerGeoServiceBlockingStub.getAllCountries(Empty.getDefaultInstance()).getAllCountriesList());
    }
    return cachedCountries;
  }
}
