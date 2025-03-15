package rangiffler.test.grpc.userdata;


import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.grpc.UserIdsResponse;
import rangiffler.grpc.UserRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.model.testdata.TestUser;

import java.util.List;

import static io.qameta.allure.SeverityLevel.CRITICAL;

@DisplayName("[GRPC] Userdata")
class GetUserFriendsIdsTest extends GrpcUserdataTestBase {

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
    @DisplayName("[GRPC] Проверка получения списка всех друзей")
    void shouldReturnAllUserFriendIdsTest(TestUser user) {
        final UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .build();
        final UserIdsResponse response = blockingStub.getUserFriendsIds(request);

        Assertions.assertNotNull(response, "Ответ не должен быть null");
        Assertions.assertEquals(user.getFriends().size(), response.getUserIdsCount(),
                "Количество возвращённых id должно быть равно количеству друзей");
        Assertions.assertTrue(response.getUserIdsList().containsAll(
                        List.of(user.getFriends().get(0).getId().toString(), user.getFriends().get(1).getId().toString())),
                "Возвращённые id должны соответствовать id созданных друзей");
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
    @DisplayName("[GRPC] Проверка отсутствия неподтвержденных друзей в списке")
    void shouldExcludePendingFriendsIdsTest(TestUser user) {
        final UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .build();
        final UserIdsResponse response = blockingStub.getUserFriendsIds(request);

        Assertions.assertNotNull(response, "Ответ не должен быть null");
        Assertions.assertEquals(1, response.getUserIdsCount(),
                "В ответе должен быть только id подтверждённых друзей");
        Assertions.assertTrue(response.getUserIdsList().contains(user.getFriends().getFirst().getId().toString()),
                "Возвращённые id должны соответствовать id только подтверждённого друга");
    }
}
