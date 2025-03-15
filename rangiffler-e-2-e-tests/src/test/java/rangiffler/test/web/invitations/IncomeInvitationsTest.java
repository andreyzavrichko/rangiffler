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
import static io.qameta.allure.SeverityLevel.NORMAL;

@WebTest
@DisplayName("[WEB] Входящие заявки в друзья")
class IncomeInvitationsTest {

    @Inject
    private PeoplePage peoplePage;


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка получения всех входящих заявок в друзья")
    void shouldIncomeInvitationsTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .usersCountShouldBeEqualTo(2)
                .usersShouldBePresentedInTable(user.getIncomeInvitations().get(0), user.getIncomeInvitations().get(1));
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения входящих заявок в друзья пользователя с фильтрацией по username")
    void shouldIncomeInvitationsWithUsernameFilterTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .search(user.getIncomeInvitations().getFirst().getUsername())
                .exactlyUsersShouldBePresentedInTable(user.getIncomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения входящих заявок в друзья пользователя с фильтрацией по firstname")
    void shouldIncomeInvitationsWithFirstnameFilterTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .search(user.getIncomeInvitations().getFirst().getFirstname())
                .exactlyUsersShouldBePresentedInTable(user.getIncomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend(pending = true)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка получения входящих заявок в друзья пользователя с фильтрацией по lastname")
    void shouldIncomeInvitationsWithLastnameFilterTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .search(user.getIncomeInvitations().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getIncomeInvitations().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend(pending = true),
                    @Friend,
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Входящие заявки")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("[WEB] Проверка отсутствия друзей и исходящих заявок в списке входящих заявок в друзья")
    void shouldOutcomeInvitationAndFriendsFriendsTest(TestUser user) {
        peoplePage.open()
                .openIncomeInvitationsTab()
                .search(user.getIncomeInvitations().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getIncomeInvitations().getFirst());
    }
}
