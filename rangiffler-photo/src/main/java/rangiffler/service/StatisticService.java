package rangiffler.service;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import rangiffler.data.repository.StatisticRepository;
import rangiffler.grpc.CountryStatisticResponse;
import rangiffler.grpc.RangifflerStatisticServiceGrpc;
import rangiffler.grpc.StatisticRequest;
import rangiffler.grpc.StatisticResponse;

import java.util.UUID;

@GrpcService
public class StatisticService extends RangifflerStatisticServiceGrpc.RangifflerStatisticServiceImplBase {

  private final StatisticRepository statisticRepository;

  @Autowired
  public StatisticService(StatisticRepository statisticRepository) {
    this.statisticRepository = statisticRepository;
  }

  @Override
  public void getStatistic(StatisticRequest request, StreamObserver<StatisticResponse> responseObserver) {
    var statistic = statisticRepository.countStatistic(
        request.getUserIdsList().stream().map(UUID::fromString).toList()
    );

    var statisticResponse = StatisticResponse.newBuilder()
        .addAllStatistic(
            statistic.stream().map(entity -> CountryStatisticResponse.newBuilder()
                .setCount(entity.getCount().intValue())
                .setCountryId(entity.getCountryId().toString())
                .build()
            ).toList()
        )
        .build();

    responseObserver.onNext(statisticResponse);
    responseObserver.onCompleted();
  }
}
