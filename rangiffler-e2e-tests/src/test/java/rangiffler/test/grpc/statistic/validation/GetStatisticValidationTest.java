package rangiffler.test.grpc.statistic.validation;

import com.google.inject.Inject;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.CountryRepository;
import rangiffler.grpc.StatisticRequest;
import rangiffler.grpc.StatisticResponse;
import rangiffler.test.grpc.statistic.GrpcStatisticTestBase;

import java.util.List;

import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("[GRPC] Statistic")
class GetStatisticValidationTest extends GrpcStatisticTestBase {

    @Inject
    private CountryRepository countryRepository;

    @Test
    @Story("Получение статистики")
    @Feature("Статистика")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("statistic"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения статистики с неверным ID пользователя")
    void shouldGetStatisticWithInvalidUserIdTest() {
        final StatisticRequest request = StatisticRequest.newBuilder()
                .addAllUserIds(List.of("invalid-user-id"))
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.getStatistic(request));
        assertEquals("ABORTED", exception.getStatus().getCode().name(),
                "Ожидаем ошибку ABORTED для неверного ID пользователя");
    }


    @Test
    @Story("Получение статистики")
    @Feature("Статистика")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("statistic"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения статистики с пустым списком пользователя")
    void shouldGetStatisticWithEmptyUserListTest() {
        final StatisticRequest request = StatisticRequest.newBuilder()
                .addAllUserIds(List.of())
                .build();

        StatisticResponse response = blockingStub.getStatistic(request);

        assertEquals(0, response.getStatisticCount(),
                "Ожидаем, что сервер вернет пустую статистику при передаче пустого списка пользователей");
    }



    @Test
    @Story("Получение статистики")
    @Feature("Статистика")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("statistic"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения статистики без списка пользователей")
    void shouldGetStatisticWithEmptyTest() {
        final StatisticRequest request = StatisticRequest.newBuilder().build();

        StatisticResponse response = blockingStub.getStatistic(request);

        assertEquals(0, response.getStatisticCount(),
                "Ожидаем, что сервер вернет пустую статистику при некорректном запросе");
    }


}
