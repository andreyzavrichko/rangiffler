package rangiffler.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rangiffler.grpc.RangifflerStatisticServiceGrpc;
import rangiffler.grpc.StatisticRequest;
import rangiffler.model.GqlCountry;
import rangiffler.model.GqlStat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс для взаимодействия с gRPC сервисом статистики.
 * Предоставляет методы для получения статистики пользователей, включая статистику друзей.
 */
@Component
public class StatisticClient {

  @GrpcClient("grpcPhotoClient")
  private RangifflerStatisticServiceGrpc.RangifflerStatisticServiceBlockingStub rangifflerStatisticServiceBlockingStub;
  @Autowired
  private UsersClient usersClient;

  /**
   * Получает статистику пользователя, включая статистику друзей, если флаг withFriends установлен в true.
   *
   * @param userName Имя пользователя для получения статистики.
   * @param withFriends Флаг для включения статистики друзей.
   * @return Список статистических данных для указанного пользователя.
   */
  public List<GqlStat> getStatistic(String userName, boolean withFriends) {
    var userIds = new ArrayList<UUID>();
    userIds.add(usersClient.getUser(userName).id());

    // Добавляем идентификаторы друзей, если флаг withFriends установлен в true
    if (withFriends) {
      userIds.addAll(usersClient.getUserFriendsIds(userName));
    }

    var response = rangifflerStatisticServiceBlockingStub.getStatistic(
            StatisticRequest.newBuilder().addAllUserIds(userIds.stream().map(UUID::toString).toList()).build()
    );

    // Преобразуем статистику из gRPC ответа в модель GqlStat
    return response.getStatisticList().stream()
            .map(s -> new GqlStat(s.getCount(), new GqlCountry(UUID.fromString(s.getCountryId()), null, null, null)))
            .toList();
  }
}
