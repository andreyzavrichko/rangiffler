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


@DisplayName("[GRPC] Userdata")
class GetUserFriendsOutcomeRequestsValidationTest extends GrpcUserdataTestBase {

    @Test
    @CreateUser(
            friends = {}
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка отсутствия исходящих запросов на дружбу")
    void shouldNoOutgoingFriendTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(0, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка отсутствия исходящих запросов на дружбу при поиске по невалидному username")
    void shouldInvalidUsernameFilterTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("invalidUsername")
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(0, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка отсутствия исходящих запросов на дружбу при поиске по несуществующему username")
    void shouldSearchQueryNoMatchTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("nonExistentName")
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(0, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка ошибки при Page -1")
    void shouldInvalidPageNumberTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(-1)
                .setSize(10)
                .build();

        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.getFriendshipAddresses(request);
        });

        Assertions.assertTrue(exception.getMessage().contains("INVALID_ARGUMENT"),
                "Expected INVALID_ARGUMENT error but got: " + exception.getMessage());
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend,
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.INCOME)
            }
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка отсутствия друзей и исходящих запросов в списке исходящих")
    void shouldExcludeFriendsAndIncomeTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(1, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertFalse(response.getAllUsersList().stream()
                .anyMatch(u -> u.getUsername().equals(user.getIncomeInvitations().getFirst().getUsername())));
        Assertions.assertFalse(response.getAllUsersList().stream()
                .anyMatch(u -> u.getUsername().equals(user.getFriends().getFirst().getUsername())));
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка пустого ответа при несуществующей поисковом параметре")
    void shouldEmptyResponseForSearchQueryTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("someSearchTermThatDoesNotExist")
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(0, response.getAllUsersCount()); // No results expected
        Assertions.assertFalse(response.getHasNext());
    }

}
