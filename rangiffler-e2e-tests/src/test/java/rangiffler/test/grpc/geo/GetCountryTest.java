package rangiffler.test.grpc.geo;


import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.Country;
import rangiffler.grpc.GetCountryByCodeRequest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.NORMAL;

@DisplayName("Geo")
class GetCountryTest extends GrpcGeoTestBase {


    @Test
    @Story("Список стран по коду")
    @Feature("Гео")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("geo"), @Tag("smoke")})
    @DisplayName("Проверка получения страны по коду")
    void shouldGetCountryByCodeTest() {
        final Country response = blockingStub.getCountryByCode(
                GetCountryByCodeRequest.newBuilder()
                        .setCode("us")
                        .build()
        );

        Assertions.assertNotNull(response, "Response is null");
        Assertions.assertFalse(response.getId().isEmpty(), "ID is empty");
        Assertions.assertEquals("us", response.getCode(), "Country code is incorrect");
        Assertions.assertEquals("United States", response.getName(), "Country name is incorrect");
        Assertions.assertFalse(response.getFlag().isEmpty(), "Flag is empty");
    }

    @Test
    @Story("Список стран по коду")
    @Feature("Гео")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("geo"), @Tag("regress")})
    @DisplayName("Проверка получения страны по пустому коду")
    void shouldGetCountryByCodeNotFoundTest() {
        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> blockingStub.getCountryByCode(
                        GetCountryByCodeRequest.newBuilder()
                                .setCode("")
                                .build()
                ),
                "Expected NOT_FOUND exception was not thrown"
        );

        Assertions.assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode(), "Incorrect gRPC status code");
        Assertions.assertTrue(exception.getMessage().contains("Страна не найдена"), "Unexpected error message: " + exception.getMessage());
    }


    @Test
    @Story("Список стран по коду")
    @Feature("Гео")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("geo"), @Tag("regress")})
    @DisplayName("Проверка получения страны по пробельному коду")
    void shouldGetCountryByCodeNotFoundSpaceTest() {
        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> blockingStub.getCountryByCode(
                        GetCountryByCodeRequest.newBuilder()
                                .setCode(" ")
                                .build()
                ),
                "Expected NOT_FOUND exception was not thrown"
        );

        Assertions.assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode(), "Incorrect gRPC status code");
        Assertions.assertTrue(exception.getMessage().contains("Страна не найдена"), "Unexpected error message: " + exception.getMessage());
    }


    @Test
    @Story("Список стран по коду")
    @Feature("Гео")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("geo"), @Tag("regress")})
    @DisplayName("Проверка получения страны по несуществующему коду")
    void getCountryByCodeNotFound111111Test() {
        StatusRuntimeException exception = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> blockingStub.getCountryByCode(
                        GetCountryByCodeRequest.newBuilder()
                                .setCode("11111111")
                                .build()
                ),
                "Expected NOT_FOUND exception was not thrown"
        );

        Assertions.assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode(), "Incorrect gRPC status code");
        Assertions.assertTrue(exception.getMessage().contains("Страна не найдена"), "Unexpected error message: " + exception.getMessage());
    }


}
