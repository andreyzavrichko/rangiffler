package rangiffler.test.grpc.geo;


import com.google.protobuf.Empty;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.AllCountriesResponse;
import rangiffler.grpc.Country;

import java.util.List;

import static io.qameta.allure.SeverityLevel.BLOCKER;


@DisplayName("Geo")
class GetAllCountriesTest extends GrpcGeoTestBase {


    @Test
    @Story("Список стран")
    @Feature("Гео")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("geo"), @Tag("smoke")})
    @DisplayName("Проверка получения списка всех стран")
    void shouldAllCountriesTest() {
        AllCountriesResponse response = blockingStub.getAllCountries(Empty.getDefaultInstance());

        Assertions.assertNotNull(response, "Response is null");
        List<Country> countries = response.getAllCountriesList();
        Assertions.assertFalse(countries.isEmpty(), "Country list is empty");

        countries.forEach(country ->
                Assertions.assertAll(
                        () -> Assertions.assertNotNull(country.getFlag(), "Flag is missing for country: " + country.getName()),
                        () -> Assertions.assertNotNull(country.getCode(), "Code is missing for country: " + country.getName()),
                        () -> Assertions.assertNotNull(country.getName(), "Name is missing for country: " + country.getCode()),
                        () -> Assertions.assertNotNull(country.getId(), "ID is missing for country: " + country.getName())
                )
        );
    }


}
