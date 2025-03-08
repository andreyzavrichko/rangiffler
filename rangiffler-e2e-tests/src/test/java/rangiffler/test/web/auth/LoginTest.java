package rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.page.LoginPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static rangiffler.utils.RandomDataUtils.randomUsername;


@DisplayName("Авторизация")
public class LoginTest {

    @Test
    @Story("Неуспешная авторизация")
    @Feature("Авторизация")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("auth"), @Tag("smoke")})
    @DisplayName("Проверка неуспешной авторизации с незарегистрированным пользователем")
    void shouldUserLoginWithBadCredentialsTest() {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(randomUsername(), "BAD")
                .submit(new LoginPage())
                .checkError("Неверные учетные данные пользователя");
    }


}