package rangiffler.test.grpc.userdata.validation;

import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.AllUsersRequest;
import rangiffler.grpc.AllUsersResponse;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import static io.qameta.allure.SeverityLevel.NORMAL;

@DisplayName("Userdata")
class GetUserFriendsValidationTest extends GrpcUserdataTestBase {


    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка валидации пустого имени пользователя")
    void shouldReturnErrorForEmptyUsernameTest() {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername("")
                .setPage(0)
                .setSize(10)
                .build();

        Assertions.assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.getUserFriends(request);
        }, "Должна быть ошибка для пустого имени пользователя");
    }

    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка валидации некорректного фильтра по имени")
    void shouldReturnErrorForInvalidFirstnameFilterTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("InvalidFirstname")
                .setPage(0)
                .setSize(10)
                .build();

        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, response.getAllUsersCount(), "Друзья не должны быть найдены по некорректному имени"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы")
        );
    }

    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка валидации некорректного фильтра по фамилии")
    void shouldReturnErrorForInvalidLastnameFilterTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("InvalidLastname")
                .setPage(0)
                .setSize(10)
                .build();

        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, response.getAllUsersCount(), "Друзья не должны быть найдены по некорректной фамилии"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы")
        );
    }

    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка валидации некорректного размера страницы")
    void shouldReturnErrorForInvalidPageSizeTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(-1)
                .build();

        Assertions.assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.getUserFriends(request);
        }, "Должна быть ошибка для некорректного размера страницы");
    }


    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка валидации страницы с нулевым значением")
    void shouldReturnErrorForZeroPageSizeTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(0)
                .build();

        Assertions.assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.getUserFriends(request);
        }, "Должна быть ошибка для страницы с нулевым размером");
    }

}
