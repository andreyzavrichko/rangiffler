package rangiffler.test.grpc.userdata.validation;

import com.google.inject.Inject;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.FriendshipRepository;
import rangiffler.db.repository.UserRepository;
import rangiffler.grpc.FriendshipAction;
import rangiffler.grpc.UpdateUserFriendshipRequest;
import rangiffler.jupiter.annotation.CreateExtrasUsers;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Extras;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Userdata")
class UpdateUserFriendshipValidationTest extends GrpcUserdataTestBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FriendshipRepository friendshipRepository;

    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка отправки заявки в друзья с несуществующим получателем")
    void shouldSendFriendRequestToNonExistentUserTest(TestUser mainUser) {
        String nonExistentUserId = "00000000-0000-0000-0000-000000000000";

        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(mainUser.getId().toString())
                .setActionTargetUserId(nonExistentUserId)
                .setAction(FriendshipAction.ADD)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.updateUserFriendship(request),
                "Ожидается ошибка при отправке заявки в друзья несуществующему пользователю");
    }


    @CreateExtrasUsers(@CreateUser)
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка отправки заявки в друзья от несуществующего пользователя")
    void shouldSendFriendRequestFromNonExistentUserTest(@Extras TestUser[] additionalUsers) {
        String nonExistentUserId = "00000000-0000-0000-0000-000000000001";

        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(nonExistentUserId)
                .setActionTargetUserId(additionalUsers[0].getId().toString())
                .setAction(FriendshipAction.ADD)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.updateUserFriendship(request),
                "Ожидается ошибка при отправке заявки от несуществующего пользователя");
    }


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка принятия заявки, когда приглашение отсутствует")
    void shouldAcceptFriendRequestWithoutInvitationTest(TestUser user) {
        String randomInviterId = "00000000-0000-0000-0000-000000000002";

        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(user.getId().toString())
                .setActionTargetUserId(randomInviterId)
                .setAction(FriendshipAction.ACCEPT)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.updateUserFriendship(request),
                "Ожидается ошибка при принятии заявки, когда приглашение отсутствует");
    }


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка отклонения заявки, когда приглашение отсутствует")
    void shouldRejectFriendRequestWithoutInvitationTest(TestUser user) {
        String randomInviterId = "00000000-0000-0000-0000-000000000003";

        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(user.getId().toString())
                .setActionTargetUserId(randomInviterId)
                .setAction(FriendshipAction.REJECT)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.updateUserFriendship(request),
                "Ожидается ошибка при отклонении заявки, когда приглашение отсутствует");
    }


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("Проверка удаления несуществующей дружбы")
    void shouldDeleteFriendWhenNotFriendsTest(TestUser user) {
        String nonFriendId = "00000000-0000-0000-0000-000000000004";

        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(user.getId().toString())
                .setActionTargetUserId(nonFriendId)
                .setAction(FriendshipAction.DELETE)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.updateUserFriendship(request),
                "Ожидается ошибка при попытке удалить дружбу, когда она отсутствует");
    }
}
