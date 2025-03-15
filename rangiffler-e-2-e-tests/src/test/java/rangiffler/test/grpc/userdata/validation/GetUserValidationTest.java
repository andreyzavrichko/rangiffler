package rangiffler.test.grpc.userdata.validation;


import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.UserByIdRequest;
import rangiffler.grpc.UserRequest;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("[GRPC] Userdata")
class GetUserValidationTest extends GrpcUserdataTestBase {


    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с пустым username")
    void shouldReturnErrorForEmptyUsernameTest() {
        var request = UserRequest.newBuilder().setUsername("").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUser(request),
                "Ожидалась ошибка при запросе пользователя с пустым username");
    }

    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с несуществующим username")
    void shouldReturnErrorForUnknownUsernameTest() {
        var request = UserRequest.newBuilder().setUsername("unknownUser").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUser(request),
                "Ожидалась ошибка при запросе пользователя с несуществующим username");
    }


    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с пустым ID")
    void shouldReturnErrorForEmptyUserIdTest() {
        var request = UserByIdRequest.newBuilder().setId("").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUserById(request),
                "Ожидалась ошибка при запросе пользователя с пустым ID");
    }

    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с null username")
    void shouldReturnErrorForNullUsernameTest() {
        var request = UserRequest.newBuilder().build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUser(request),
                "Ожидалась ошибка при запросе пользователя с null username");
    }


    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с несуществующим id")
    void shouldReturnErrorForNonExistentUserIdTest() {
        var request = UserByIdRequest.newBuilder().setId(UUID.randomUUID().toString()).build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUserById(request),
                "Ожидалась ошибка при запросе пользователя с несуществующим ID");
    }


    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с id, состоящим из пробелов")
    void shouldReturnErrorForWhitespaceUserIdTest() {
        var request = UserByIdRequest.newBuilder().setId("   ").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUserById(request),
                "Ожидалась ошибка при запросе пользователя с ID, содержащим только пробелы");
    }


    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с очень длинным username")
    void shouldReturnErrorForOverlyLongUsernameTest() {
        var request = UserRequest.newBuilder()
                .setUsername("a".repeat(300))
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUser(request),
                "Ожидалась ошибка при запросе пользователя с очень длинным username");
    }

    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с некорректным id")
    void shouldReturnErrorForInvalidUserIdTest() {
        var request = UserByIdRequest.newBuilder().setId("invalid_uuid").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUserById(request),
                "Ожидалась ошибка при запросе пользователя с некорректным ID");
    }

    @Test
    @Story("Получение пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка запроса пользователя с id, содержащим спецсимволы")
    void shouldReturnErrorForUserIdWithSpecialCharactersTest() {
        var request = UserByIdRequest.newBuilder().setId("!@#$%^&*()").build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.getUserById(request),
                "Ожидалась ошибка при запросе пользователя с ID, содержащим спецсимволы");
    }

}
