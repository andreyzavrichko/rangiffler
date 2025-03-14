package rangiffler.test.web.invitations;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.PeoplePage;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.MINOR;

@WebTest
@DisplayName("[WEB] Исходящие заявки в друзья")
class OutcomeInvitationsTest {

    @Inject
    private PeoplePage peoplePage;


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка получения всех исходящих заявок в друзья")
    void shouldOutcomeInvitationsListTest(TestUser user) {
        peoplePage.open()
                .openOutcomeInvitationsTab()
                .usersCountShouldBeEqualTo(2)
                .usersShouldBePresentedInTable(user.getOutcomeInvitations().get(0), user.getOutcomeInvitations().get(1));
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(MINOR)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения исходящих заявок в друзья пользователя с фильтрацией по username")
    void shouldOutcomeInvitationsWithUsernameFilterTest(TestUser user) {
        peoplePage.open()
                .openOutcomeInvitationsTab()
                .search(user.getOutcomeInvitations().getFirst().getUsername())
                .exactlyUsersShouldBePresentedInTable(user.getOutcomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(MINOR)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения исходящих заявок в друзья пользователя с фильтрацией по firstname")
    void shouldOutcomeInvitationsWithFirstnameFilterTest(TestUser user) {
        peoplePage.open()
                .openOutcomeInvitationsTab()
                .search(user.getOutcomeInvitations().getFirst().getFirstname())
                .exactlyUsersShouldBePresentedInTable(user.getOutcomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(MINOR)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения исходящих заявок в друзья пользователя с фильтрацией по lastname")
    void shouldOutcomeInvitationsWithLastnameFilterTest(TestUser user) {
        peoplePage.open()
                .openOutcomeInvitationsTab()
                .search(user.getOutcomeInvitations().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getOutcomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
                    @Friend,
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.INCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(MINOR)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка отсутствия друзей и исходящих заявок в списке исходящих заявок в друзья")
    void shouldOutcomeInvitationFriendsListTest(TestUser user) {
        peoplePage.open()
                .openOutcomeInvitationsTab()
                .search(user.getOutcomeInvitations().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getOutcomeInvitations().getFirst());
    }
}
