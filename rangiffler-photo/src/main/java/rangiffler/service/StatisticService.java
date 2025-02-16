package rangiffler.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import rangiffler.data.repository.StatisticRepository;
import rangiffler.grpc.CountryStatisticResponse;
import rangiffler.grpc.RangifflerStatisticServiceGrpc;
import rangiffler.grpc.StatisticRequest;
import rangiffler.grpc.StatisticResponse;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с запросами статистики по пользователям и странам.
 * Этот сервис предоставляет статистику о количестве фотографий для каждого пользователя по странам.
 */
@GrpcService
public class StatisticService extends RangifflerStatisticServiceGrpc.RangifflerStatisticServiceImplBase {

  private final StatisticRepository statisticRepository;

  /**
   * Конструктор для инициализации репозитория статистики.
   *
   * @param statisticRepository репозиторий для работы с данными статистики.
   */
  @Autowired
  public StatisticService(StatisticRepository statisticRepository) {
    this.statisticRepository = statisticRepository;
  }

  /**
   * Получает статистику по количеству фотографий для пользователей по странам.
   *
   * @param request запрос с перечнем идентификаторов пользователей для получения статистики.
   * @param responseObserver наблюдатель для отправки ответа с результатами статистики.
   */
  @Override
  public void getStatistic(StatisticRequest request, StreamObserver<StatisticResponse> responseObserver) {
    // Преобразуем список идентификаторов пользователей из строковых значений в UUID и получаем статистику.
    List<CountryStatisticResponse> statistics = statisticRepository.countStatistic(
                    request.getUserIdsList().stream()
                            .map(UUID::fromString)
                            .toList()
            ).stream()
            .map(entity -> CountryStatisticResponse.newBuilder()
                    .setCount(entity.getCount().intValue()) // Преобразуем количество в целое число
                    .setCountryId(entity.getCountryId().toString()) // Преобразуем UUID страны в строку
                    .build()
            ).toList();

    // Создаем и отправляем ответ с найденной статистикой.
    StatisticResponse statisticResponse = StatisticResponse.newBuilder()
            .addAllStatistic(statistics) // Добавляем все статистические данные
            .build();

    responseObserver.onNext(statisticResponse);
    responseObserver.onCompleted();
  }
}
