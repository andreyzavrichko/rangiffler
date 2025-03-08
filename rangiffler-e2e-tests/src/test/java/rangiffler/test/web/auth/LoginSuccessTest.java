package rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.extension.BrowserExtension;
import rangiffler.jupiter.extension.GuiceExtension;
import rangiffler.jupiter.extension.UserExtension;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.LoginPage;
import rangiffler.page.MainPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;


@DisplayName("Авторизация")
@ExtendWith({
        GuiceExtension.class,
        AllureJunit5.class,
        UserExtension.class,
        BrowserExtension.class
})
public class LoginSuccessTest {

    @Test
    @Story("Успешная авторизация")
    @Feature("Авторизация")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("auth"), @Tag("smoke")})
    @CreateUser
    @DisplayName("Проверка успешной авторизации")
    void shouldUserSuccessAuthenticationTest(TestUser user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.getUsername(), user.getTestData().password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }


}