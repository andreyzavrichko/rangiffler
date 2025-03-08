package rangiffler.test.grpc.userdata;


import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.AllUsersRequest;
import rangiffler.grpc.AllUsersResponse;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.mapper.UserEntityMapper;
import rangiffler.model.testdata.TestUser;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.*;
import static rangiffler.grpc.FriendStatus.*;


@DisplayName("Userdata")
class GetUserFriendsIncomeRequestsTest extends GrpcUserdataTestBase {


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Получение списка заявок на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения входящих заявок на дружбу")
    void shouldRetrieveAllFriendshipTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(2, response.getAllUsersCount());
        assertFalse(response.getHasNext());
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), INVITATION_RECEIVED)));
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().get(1), INVITATION_RECEIVED)));
    }

    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Получение списка заявок на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения входящих заявок на дружбу с фильтром по username")
    void shouldGetFilteredFriendshipRequestsByUsernameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getIncomeInvitations().getFirst().getUsername())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(1, response.getAllUsersCount());
        assertFalse(response.getHasNext());
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), INVITATION_RECEIVED)));
        assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().get(1), INVITATION_RECEIVED)));
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Получение списка заявок на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения входящих заявок на дружбу с фильтром по firstName")
    void getFilteredFriendshipRequestsByFirstNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getIncomeInvitations().getFirst().getFirstname())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(1, response.getAllUsersCount());
        assertFalse(response.getHasNext());
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), INVITATION_RECEIVED)));
        assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().get(1), INVITATION_RECEIVED)));
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Получение списка заявок на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка получения входящих заявок на дружбу с фильтром по lastName")
    void getFilteredFriendshipRequestsByLastNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery(user.getIncomeInvitations().getFirst().getLastName())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(1, response.getAllUsersCount());
        assertFalse(response.getHasNext());
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), INVITATION_RECEIVED)));
        assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().get(1), INVITATION_RECEIVED)));
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend,
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Получение списка заявок на дружбу")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка отсутствия друзей и исходящих заявок в списке входящих")
    void shouldAbsencePendingFriendsInIncomeInvitationsTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(1, response.getAllUsersCount());
        assertFalse(response.getHasNext());
        assertTrue(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getIncomeInvitations().getFirst(), INVITATION_RECEIVED)));
        assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getOutcomeInvitations().getFirst(), INVITATION_SENT)));
        assertFalse(response.getAllUsersList().contains(UserEntityMapper.toMessage(user.getFriends().getFirst(), FRIEND)));
    }
}
