package rangiffler.test.grpc.userdata;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.model.FriendshipStatus;
import rangiffler.db.repository.FriendshipRepository;
import rangiffler.db.repository.UserRepository;
import rangiffler.grpc.FriendStatus;
import rangiffler.grpc.FriendshipAction;
import rangiffler.grpc.UpdateUserFriendshipRequest;
import rangiffler.grpc.User;
import rangiffler.jupiter.annotation.CreateExtrasUsers;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Extras;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.model.testdata.TestUser;

import java.time.Duration;
import java.util.Optional;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Userdata")
class UpdateUserFriendshipTest extends GrpcUserdataTestBase {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FriendshipRepository friendshipRepository;

    @CreateExtrasUsers(@CreateUser)
    @CreateUser
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка отправка заявки в друзья")
    void shouldSendFriendTest(TestUser mainUser, @Extras TestUser[] additionalUsers) {
        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(mainUser.getId().toString())
                .setActionTargetUserId(additionalUsers[0].getId().toString())
                .setAction(FriendshipAction.ADD)
                .build();

        User response = blockingStub.updateUserFriendship(request);

        assertAll("Проверка данных ответа",
                () -> assertEquals(additionalUsers[0].getId().toString(), response.getId()),
                () -> assertEquals(additionalUsers[0].getUsername(), response.getUsername()),
                () -> assertEquals(additionalUsers[0].getFirstname(), response.getFirstname()),
                () -> assertEquals(additionalUsers[0].getLastName(), response.getLastName()),
                () -> assertEquals(additionalUsers[0].getCountry().getId().toString(), response.getCountryId()),
                () -> assertEquals(FriendStatus.INVITATION_SENT, response.getFriendStatus())
        );

        var friendshipRecord = Awaitility.await()
                .atMost(Duration.ofMillis(5000))
                .pollInterval(Duration.ofMillis(1000))
                .ignoreExceptions()
                .until(
                        () -> friendshipRepository.findFriendshipByRequesterIdAndAddresseeId(mainUser.getId(), additionalUsers[0].getId()),
                        Optional::isPresent
                ).orElseThrow();

        assertEquals(FriendshipStatus.PENDING, friendshipRecord.getStatus(), "Статус заявки в друзья");
    }


    @CreateUser(
            friends = {
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка принятия заявки в друзья")
    void shouldAcceptFriendTest(TestUser userWithInvitation) {
        TestUser inviter = userWithInvitation.getIncomeInvitations().getFirst();
        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(userWithInvitation.getId().toString())
                .setActionTargetUserId(inviter.getId().toString())
                .setAction(FriendshipAction.ACCEPT)
                .build();

        User response = blockingStub.updateUserFriendship(request);

        assertAll("Проверка данных ответа",
                () -> assertEquals(inviter.getId().toString(), response.getId()),
                () -> assertEquals(inviter.getUsername(), response.getUsername()),
                () -> assertEquals(inviter.getFirstname(), response.getFirstname()),
                () -> assertEquals(inviter.getLastName(), response.getLastName()),
                () -> assertEquals(inviter.getCountry().getId().toString(), response.getCountryId()),
                () -> assertEquals(FriendStatus.FRIEND, response.getFriendStatus())
        );

        var friendshipRecord = Awaitility.await()
                .atMost(Duration.ofMillis(5000))
                .pollInterval(Duration.ofMillis(1000))
                .ignoreExceptions()
                .until(
                        () -> friendshipRepository.findFriendshipByRequesterIdAndAddresseeId(inviter.getId(), userWithInvitation.getId()),
                        Optional::isPresent
                ).orElseThrow();

        assertEquals(FriendshipStatus.ACCEPTED, friendshipRecord.getStatus(), "Статус после принятия заявки");

    }


    @CreateUser(
            friends = {
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка отклонения заявки в друзья")
    void shouldRejectFriendTest(TestUser userWithInvitation) {
        TestUser inviter = userWithInvitation.getIncomeInvitations().getFirst();
        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(userWithInvitation.getId().toString())
                .setActionTargetUserId(inviter.getId().toString())
                .setAction(FriendshipAction.REJECT)
                .build();

        User response = blockingStub.updateUserFriendship(request);

        assertAll("Проверка данных ответа",
                () -> assertEquals(inviter.getId().toString(), response.getId()),
                () -> assertEquals(inviter.getUsername(), response.getUsername()),
                () -> assertEquals(inviter.getFirstname(), response.getFirstname()),
                () -> assertEquals(inviter.getLastName(), response.getLastName()),
                () -> assertEquals(inviter.getCountry().getId().toString(), response.getCountryId()),
                () -> assertEquals(FriendStatus.NOT_FRIEND, response.getFriendStatus())
        );

        assertDoesNotThrow(() ->
                        Awaitility.await("Ожидание удаления заявки")
                                .atMost(Duration.ofMillis(10000))
                                .pollInterval(Duration.ofMillis(1000))
                                .ignoreExceptions()
                                .until(() ->
                                        friendshipRepository.findFriendshipByRequesterIdAndAddresseeId(inviter.getId(), userWithInvitation.getId()).isEmpty()
                                ),
                "Проверка отсутствия заявки в БД"
        );

    }


    @CreateUser(
            friends = {
                    @Friend
            }
    )
    @Test
    @Story("Запросы в друзья")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("Проверка удаления из друзей")
    void shouldDeleteFriendTest(TestUser userWithFriend) {
        TestUser friend = userWithFriend.getFriends().getFirst();
        UpdateUserFriendshipRequest request = UpdateUserFriendshipRequest.newBuilder()
                .setActionAuthorUserId(userWithFriend.getId().toString())
                .setActionTargetUserId(friend.getId().toString())
                .setAction(FriendshipAction.DELETE)
                .build();

        User response = blockingStub.updateUserFriendship(request);

        assertAll("Проверка данных ответа",
                () -> assertEquals(friend.getId().toString(), response.getId()),
                () -> assertEquals(friend.getUsername(), response.getUsername()),
                () -> assertEquals(friend.getFirstname(), response.getFirstname()),
                () -> assertEquals(friend.getLastName(), response.getLastName()),
                () -> assertEquals(friend.getCountry().getId().toString(), response.getCountryId()),
                () -> assertEquals(FriendStatus.NOT_FRIEND, response.getFriendStatus())
        );

        assertDoesNotThrow(() ->
                        Awaitility.await("Ожидание удаления записи о дружбе")
                                .atMost(Duration.ofMillis(10000))
                                .pollInterval(Duration.ofMillis(1000))
                                .ignoreExceptions()
                                .until(() ->
                                        friendshipRepository.findFriendshipByRequesterIdAndAddresseeId(friend.getId(), userWithFriend.getId()).isEmpty()
                                ),
                "Проверка удаления дружбы из БД"
        );

    }
}
