package rangiffler.test.grpc.userdata.validation;


import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.UserIdsResponse;
import rangiffler.grpc.UserRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import static io.qameta.allure.SeverityLevel.NORMAL;

@DisplayName("[GRPC] Userdata")
class GetUserFriendsIdsValidationTest extends GrpcUserdataTestBase {


    @CreateUser(friends = {})
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка получения пустого списка друзей пользователя")
    void shouldReturnEmptyListForUserWithoutFriendsTest(TestUser user) {
        final UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .build();
        final UserIdsResponse response = blockingStub.getUserFriendsIds(request);

        Assertions.assertNotNull(response, "Ответ не должен быть null");
        Assertions.assertEquals(0, response.getUserIdsCount(),
                "Список id друзей должен быть пустым");
    }


    @CreateUser(
            friends = {
                    @Friend(friendshipRequestType = Friend.FriendshipRequestType.INCOME),
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(NORMAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка исключения недействительных отношений друзей")
    void shouldExcludeInvalidFriendRelationshipIdsTest(TestUser user) {
        final UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .build();

        final UserIdsResponse response = blockingStub.getUserFriendsIds(request);

        Assertions.assertNotNull(response, "Ответ не должен быть null");

        Assertions.assertEquals(2, response.getUserIdsCount(),
                "В ответе должен быть только id друзей с действительным отношением");

        Assertions.assertTrue(response.getUserIdsList().contains(user.getFriends().getLast().getId().toString()),
                "Ответ должен содержать id с действительным отношением");
    }


}
