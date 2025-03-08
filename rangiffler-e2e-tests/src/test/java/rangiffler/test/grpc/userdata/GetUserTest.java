package rangiffler.test.grpc.userdata;


import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.User;
import rangiffler.grpc.UserByIdRequest;
import rangiffler.grpc.UserRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.testdata.TestUser;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Userdata")
class GetUserTest extends GrpcUserdataTestBase {


    @CreateUser
    @Test
    @Story("Получение текущего пользователя")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения пользователя по username")
    void shouldGetUserByUsernameTest(TestUser user) {
        final UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .build();
        final User response = blockingStub.getUser(request);

        assertAll("Проверка данных пользователя",
                () -> assertNotNull(response, "Ответ не должен быть null"),
                () -> assertEquals(user.getId().toString(), response.getId(), "ID пользователя не совпадает"),
                () -> assertEquals(user.getUsername(), response.getUsername(), "Username не совпадает"),
                () -> assertEquals(user.getFirstname(), response.getFirstname(), "Имя не совпадает"),
                () -> assertEquals(user.getLastName(), response.getLastName(), "Фамилия не совпадает"),
                () -> assertEquals(user.getCountry().getId().toString(), response.getCountryId(), "ID страны не совпадает")
        );
    }


    @CreateUser
    @Test
    @Story("Получение текущего пользователя")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения пользователя по id")
    void shouldGetUserByIdTest(TestUser user) {
        final UserByIdRequest request = UserByIdRequest.newBuilder()
                .setId(user.getId().toString())
                .build();
        final User response = blockingStub.getUserById(request);

        assertAll("Проверка данных пользователя",
                () -> assertNotNull(response, "Ответ не должен быть null"),
                () -> assertEquals(user.getId().toString(), response.getId(), "ID пользователя не совпадает"),
                () -> assertEquals(user.getUsername(), response.getUsername(), "Username не совпадает"),
                () -> assertEquals(user.getFirstname(), response.getFirstname(), "Имя не совпадает"),
                () -> assertEquals(user.getLastName(), response.getLastName(), "Фамилия не совпадает"),
                () -> assertEquals(user.getCountry().getId().toString(), response.getCountryId(), "ID страны не совпадает")
        );
    }
}
