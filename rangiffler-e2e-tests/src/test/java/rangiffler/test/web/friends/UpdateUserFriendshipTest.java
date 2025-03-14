package rangiffler.test.web.friends;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.jupiter.annotation.*;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.PeoplePage;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@WebTest
@DisplayName("[WEB] Дружба")
class UpdateUserFriendshipTest {

    @Inject
    private PeoplePage peoplePage;

    @CreateExtrasUsers(@CreateUser)
    @ApiLogin
    @CreateUser
    @Test
    @Story("Друзья")
    @Feature("Заявка в друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка отправки заявки в друзья")
    void shouldSentFriendshipTest(@Extras TestUser[] users) {
        peoplePage.open()
                .openAllPeopleTab()
                .search(users[0].getUsername())
                .addFriend(users[0].getUsername())
                .shouldWaitingStatus();
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Заявка в друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка принятия заявки в друзья")
    void shouldAcceptFriendshipTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .acceptInvitation(user.getIncomeInvitations().getFirst().getUsername())
                .shouldRemoveFriendButton();

    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Заявка в друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка отклонения заявки в друзья")
    void shouldRejectFriendshipTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .rejectInvitation(user.getIncomeInvitations().getFirst().getUsername())
                .shouldAddFriendButton();

    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Заявка в друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка удаления из друзей")
    void shouldDeleteFriendshipTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .deleteFriend(user.getFriends().getFirst().getUsername())
                .shouldAddFriendButton();
    }
}
