package rangiffler.test.web.people;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateExtrasUsers;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.PeoplePage;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.TRIVIAL;

@WebTest
@DisplayName("Список людей")
class PeopleListTest {

    @Inject
    private PeoplePage peoplePage;

    @CreateExtrasUsers(@CreateUser)
    @CreateUser
    @ApiLogin
    @Test
    @Story("Люди")
    @Feature("Список людей")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("people"), @Tag("smoke")})
    @DisplayName("Проверка получения всех пользователей по переданному username")
    void shouldAllPeopleWithUsernameTest(TestUser user) {
        peoplePage.open()
                .openAllPeopleTab()
                .search(user.getUsername())
                .exactlyUsersShouldBePresentedInTable();
    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Люди")
    @Feature("Список людей")
    @Severity(TRIVIAL)
    @Tags({@Tag("web"), @Tag("people"), @Tag("regress")})
    @DisplayName("Проверка получения пользователей при передаче SearchQuery username автора запроса")
    void shouldCurrentUserWithUsernameTest(TestUser user) {
        peoplePage.open()
                .openAllPeopleTab()
                .search(user.getUsername())
                .noUserYetMessageShouldBePresented();
    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Люди")
    @Feature("Список людей")
    @Severity(TRIVIAL)
    @Tags({@Tag("web"), @Tag("people"), @Tag("regress")})
    @DisplayName("Проверка получения пользователей при передаче SearchQuery username пробел")
    void shouldCurrentUserWithSpaceTest() {
        peoplePage.open()
                .openAllPeopleTab()
                .search(" ")
                .noUserYetMessageShouldBePresented();
    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Люди")
    @Feature("Список людей")
    @Severity(TRIVIAL)
    @Tags({@Tag("web"), @Tag("people"), @Tag("regress")})
    @DisplayName("Проверка получения пользователей при передаче SearchQuery username с числами")
    void shouldCurrentUserWithNumbersTest() {
        peoplePage.open()
                .openAllPeopleTab()
                .search("123")
                .noUserYetMessageShouldBePresented();
    }


}
