package rangiffler.test.web.friends;

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

import static io.qameta.allure.SeverityLevel.*;

@WebTest
@DisplayName("Список друзей")
class FriendsListTest {

    @Inject
    private PeoplePage peoplePage;


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend,
                    @Friend,
                    @Friend,
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("Проверка отображения списка друзей")
    void shouldFriendsListTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .usersCountShouldBeEqualTo(5)
                .usersShouldBePresentedInTable(user.getFriends().get(0), user.getFriends().get(1));
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей с фильтром")
    @Severity(CRITICAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("Проверка получения друзей пользователя с фильтрацией по username")
    void shouldFriendsWithUsernameFilterTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .search(user.getFriends().getFirst().getUsername())
                .exactlyUsersShouldBePresentedInTable(user.getFriends().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend,
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей с фильтром")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("Проверка получения друзей пользователя с фильтрацией по firstname")
    void shouldFriendsWithFirstnameFilterTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .search(user.getFriends().getFirst().getFirstname())
                .exactlyUsersShouldBePresentedInTable(user.getFriends().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend,
                    @Friend,
                    @Friend
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей с фильтром")
    @Severity(MINOR)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("Проверка получения друзей пользователя с фильтрацией по lastname")
    void shouldFriendsWithLastnameFilterTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .search(user.getFriends().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getFriends().getFirst());
    }


    @ApiLogin
    @CreateUser(
            friends = {
                    @Friend,
                    @Friend(pending = true),
                    @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
            }
    )
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей с фильтром")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("Проверка отсутствия неподтвержденных друзей в списке друзей пользователя")
    void shouldFriendsPendingListTest(TestUser user) {
        peoplePage.open()
                .openFriendsTab()
                .search(user.getFriends().getFirst().getLastName())
                .exactlyUsersShouldBePresentedInTable(user.getFriends().getFirst());
    }


    @ApiLogin
    @CreateUser
    @Test
    @Story("Друзья")
    @Feature("Получение списка друзей с фильтром")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("friends"), @Tag("regress")})
    @DisplayName("Проверка пустого списка друзей")
    void shouldEmptyFriendsListTest() {
        peoplePage.open()
                .openFriendsTab()
                .usersCountShouldBeEqualTo(0)
                .noUserYetMessageShouldBePresented();
    }


}
