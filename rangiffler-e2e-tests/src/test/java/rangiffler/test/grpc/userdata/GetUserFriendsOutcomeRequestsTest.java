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
import static rangiffler.grpc.FriendStatus.INVITATION_SENT;


@DisplayName("Userdata")
class GetUserFriendsOutcomeRequestsTest extends GrpcUserdataTestBase {

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Получение списка исходящих на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения всех исходящих запросов в друзья")
    void shouldOutgoingFriendTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(2, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().getFirst(), INVITATION_SENT)));
        Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().get(1), INVITATION_SENT)));
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Получение списка исходящих на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения всех исходящих запросов в друзья по username")
    void shouldFilterRequestsByUsernameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getOutcomeInvitations().getFirst().getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(1, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().getFirst(), INVITATION_SENT)));
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Получение списка исходящих на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения всех исходящих запросов в друзья по first name")
    void shouldSearchRequestsByFirstNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getOutcomeInvitations().getFirst().getFirstname())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(1, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().get(0), INVITATION_SENT)));
        Assertions.assertFalse(response.getAllUsersList().stream().anyMatch(u -> u.getUsername().equals(user.getOutcomeInvitations().get(1).getUsername())));
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Story("Получение списка исходящих на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения всех исходящих запросов в друзья по last name")
    void shouldFilterRequestsByLastNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getOutcomeInvitations().getFirst().getLastName())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(1, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().getFirst(), INVITATION_SENT)));
        Assertions.assertFalse(response.getAllUsersList().stream().anyMatch(u -> u.getUsername().equals(user.getOutcomeInvitations().get(1).getUsername())));
    }

    @Test
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend,
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.INCOME)
            }
    )
    @Story("Получение списка исходящих на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка исключения друзей и входящие запросы из списка исходящих запросов")
    void shouldExcludeFriendsAndIncomeTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();
        AllUsersResponse response = blockingStub.getFriendshipAddresses(request);

        Assertions.assertEquals(1, response.getAllUsersCount());
        Assertions.assertFalse(response.getHasNext());
        Assertions.assertFalse(response.getAllUsersList().stream().anyMatch(u -> u.getUsername().equals(user.getIncomeInvitations().getFirst().getUsername())));
        Assertions.assertFalse(response.getAllUsersList().stream().anyMatch(u -> u.getUsername().equals(user.getFriends().getFirst().getUsername())));
    }
}
