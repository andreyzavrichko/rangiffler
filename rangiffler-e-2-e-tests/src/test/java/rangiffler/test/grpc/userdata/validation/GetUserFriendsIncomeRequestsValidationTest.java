package rangiffler.test.grpc.userdata.validation;


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
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import static io.qameta.allure.SeverityLevel.NORMAL;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("[GRPC] Userdata")
class GetUserFriendsIncomeRequestsValidationTest extends GrpcUserdataTestBase {


    @CreateUser
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок на дружбу")
    void shouldNoFriendshipRequestsTest(TestUser user) {

        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(0, response.getAllUsersCount());
        assertFalse(response.getHasNext());
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок по несуществующему пользователю")
    void shouldFilterUsersByNoneExistUsernameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("nonexistentuser")
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(0, response.getAllUsersCount());
        assertFalse(response.getHasNext());
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок по несуществующему имени")
    void shouldFilterUsersByFirstNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("nonexistentfirstname")
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(0, response.getAllUsersCount());
        assertFalse(response.getHasNext());
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок по несуществующей фамилии")
    void shouldFilterUsersByLastNameTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setSearchQuery("nonexistentlastname")
                .setPage(0)
                .setSize(10)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);

        assertEquals(0, response.getAllUsersCount());
        assertFalse(response.getHasNext());
    }


    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок по пустому username")
    void shouldInvalidUsernameTest() {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername("")
                .setPage(0)
                .setSize(10)
                .build();

        assertThrows(Exception.class, () -> {
            blockingStub.getFriendshipRequests(request);
        });
    }

    @CreateUser
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения входящих заявок с большим количеством Size")
    void shouldRequestWithExcessivePageSizeTest(TestUser user) {
        AllUsersRequest request = AllUsersRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPage(0)
                .setSize(1000)
                .build();

        AllUsersResponse response = blockingStub.getFriendshipRequests(request);
        assertTrue(response.getAllUsersCount() <= 1000);
    }

}
