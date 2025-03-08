package rangiffler.test.grpc.userdata;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.AllUsersRequest;
import rangiffler.grpc.AllUsersResponse;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.mapper.UserEntityMapper;
import rangiffler.model.testdata.TestUser;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static rangiffler.grpc.FriendStatus.FRIEND;

@DisplayName("Userdata")
class GetUserFriendsTest extends GrpcUserdataTestBase {


    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Получение списка друзей")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения всех друзей")
    void shouldReturnAllUserFriendsTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, response.getAllUsersCount(), "Некорректное количество друзей"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы"),
                () -> Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().getFirst(), FRIEND)),
                        "Первый друг должен быть в ответе"),
                () -> Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().get(1), FRIEND)),
                        "Второй друг должен быть в ответе")
        );
    }

    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Получение списка друзей")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения друзей с фильтром по username")
    void shouldReturnFilteredFriendsByUsernameTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getFriends().get(0).getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.getAllUsersCount(), "Некорректное количество друзей в ответе"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы"),
                () -> Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().getFirst(), FRIEND)),
                        "Первый друг должен быть в ответе"),
                () -> Assertions.assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().get(1), FRIEND)),
                        "Второй друг не должен быть в ответе")
        );
    }


    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Получение списка друзей")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения друзей с фильтром по first name")
    void shouldReturnFilteredFriendsByFirstnameTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getFriends().get(0).getFirstname())
                .setPage(0)
                .setSize(10)
                .build();
        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.getAllUsersCount(), "Некорректное количество друзей в ответе"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы"),
                () -> Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().getFirst(), FRIEND)),
                        "Первый друг должен быть в ответе"),
                () -> Assertions.assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().get(1), FRIEND)),
                        "Второй друг не должен быть в ответе")
        );
    }


    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Получение списка друзей")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения друзей с фильтром по last name")
    void shouldReturnFilteredFriendsByLastnameTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getFriends().get(0).getLastName())
                .setPage(0)
                .setSize(10)
                .build();
        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.getAllUsersCount(), "Некорректное количество друзей в ответе"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы"),
                () -> Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().getFirst(), FRIEND)),
                        "Первый друг должен быть в ответе"),
                () -> Assertions.assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().get(1), FRIEND)),
                        "Второй друг не должен быть в ответе")
        );
    }

    @CreateUser(
            friends = {
                    @Friend,
                    @Friend(pending = true),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Получение списка друзей")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка отсутствия в списке друзей с неподтвержденным статусом дружбы")
    void shouldExcludePendingFriendsFromListTest(TestUser user) {
        final AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getFriends().getFirst().getLastName())
                .setPage(0)
                .setSize(10)
                .build();
        final AllUsersResponse response = blockingStub.getUserFriends(request);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, response.getAllUsersCount(), "Некорректное количество друзей в ответе"),
                () -> Assertions.assertFalse(response.getHasNext(), "Не должно быть следующей страницы"),
                () -> Assertions.assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), FRIEND)),
                        "Пендинговый друг по запросу дохода не должен быть в ответе"),
                () -> Assertions.assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().getFirst(), FRIEND)),
                        "Пендинговый друг по запросу исходящего не должен быть в ответе")
        );
    }
}
