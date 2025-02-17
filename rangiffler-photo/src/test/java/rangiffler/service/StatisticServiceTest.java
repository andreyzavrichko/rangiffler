package rangiffler.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import rangiffler.data.GroupedStatistic;
import rangiffler.data.repository.StatisticRepository;
import rangiffler.grpc.*;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

    @Mock
    private StatisticRepository statisticRepository;
    @Mock
    private StreamObserver<StatisticResponse> responseObserver;

    @InjectMocks
    private StatisticService statisticService;

    @Test
    void getStatisticShouldReturnGroupedData() {
        UUID userId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();
        StatisticRequest request = StatisticRequest.newBuilder()
                .addUserIds(userId.toString())
                .build();

        GroupedStatistic gs = new GroupedStatistic(countryId, 5L);
        when(statisticRepository.countStatistic(anyList())).thenReturn(List.of(gs));

        statisticService.getStatistic(request, responseObserver);

        ArgumentCaptor<StatisticResponse> captor = ArgumentCaptor.forClass(StatisticResponse.class);
        verify(responseObserver).onNext(captor.capture());

        assertEquals(5, captor.getValue().getStatisticList().get(0).getCount());
        verify(responseObserver).onCompleted();
    }
}