package rangiffler.test.grpc.statistic;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.model.CountryEntity;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.CountryStatisticResponse;
import rangiffler.grpc.StatisticRequest;
import rangiffler.grpc.StatisticResponse;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;

import java.util.List;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[GRPC] Statistic")
class GetStatisticTest extends GrpcStatisticTestBase {

    @Inject
    private PhotoRepository photoRepository;
    @Inject
    private CountryRepository countryRepository;


    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA),
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA),
                    @WithPhoto(countryCode = CountryCodes.CA, image = PhotoFiles.CHINA)
            }
    )
    @Test
    @Story("Получение статистики")
    @Feature("Статистика")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("statistic"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка статистики")
    void shouldGetStatisticTest(TestUser user) {
        final StatisticRequest request = StatisticRequest.newBuilder()
                .addAllUserIds(List.of(user.getId().toString()))
                .build();
        final StatisticResponse response = blockingStub.getStatistic(request);

        final CountryEntity cnCountry = countryRepository.findRequiredCountryByCode("cn");
        final CountryEntity caCountry = countryRepository.findRequiredCountryByCode("ca");

        final CountryStatisticResponse expectedStatisticForCn = CountryStatisticResponse.newBuilder()
                .setCountryId(cnCountry.getId().toString())
                .setCount(2)
                .build();
        final CountryStatisticResponse expectedStatisticForCa = CountryStatisticResponse.newBuilder()
                .setCountryId(caCountry.getId().toString())
                .setCount(1)
                .build();

        assertEquals(2, response.getStatisticCount(), "Количество элементов в массиве статистики");

        assertTrue(response.getStatisticList().contains(expectedStatisticForCn),
                "Статистика для страны с кодом cn отсутствует");

        assertTrue(response.getStatisticList().contains(expectedStatisticForCa),
                "Статистика для страны с кодом ca отсутствует");
    }
}
