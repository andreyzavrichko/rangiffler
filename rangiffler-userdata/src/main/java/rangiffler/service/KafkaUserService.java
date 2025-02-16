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
import rangiffler.grpc.RangifflerGeoServiceGrpc;
import rangiffler.model.UserJson;

@Component
public class KafkaUserService {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaUserService.class);
  private final UserRepository userRepository;
  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;

  @Autowired
  public KafkaUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    LOG.info("### Kafka topic [users] received message: {}", user);
    LOG.info("### Kafka consumer record: {}", cr);

    var userDataEntity = new UserEntity();
    userDataEntity.setUsername(user.username());

    var countryCode = user.countryCode();
    var countries = rangifflerGeoServiceBlockingStub.getAllCountries(Empty.getDefaultInstance()).getAllCountriesList();
    var userCountry = countries.stream()
        .filter(s -> s.getCode().equalsIgnoreCase(countryCode))
        .findFirst()
        .orElse(countries.getFirst());
    userDataEntity.setCountryId(userCountry.getId());

    var userEntity = userRepository.save(userDataEntity);
    LOG.info("### User {} successfully saved to database with id: {} and country id {}", user.username(),
        userEntity.getId(), userEntity.getCountryId());
  }
}
