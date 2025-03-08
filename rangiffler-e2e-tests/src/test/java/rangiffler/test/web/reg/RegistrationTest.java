package rangiffler.test.web.reg;

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
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.jupiter.extension.BrowserExtension;
import rangiffler.jupiter.extension.GuiceExtension;
import rangiffler.jupiter.extension.UserExtension;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.LoginPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static rangiffler.utils.RandomDataUtils.randomPassword;
import static rangiffler.utils.RandomDataUtils.randomUsername;


@DisplayName("Регистрация")
@ExtendWith({
        GuiceExtension.class,
        AllureJunit5.class,
        UserExtension.class,
        BrowserExtension.class
})
public class RegistrationTest {

    @CreateUser
    @Test
    @Story("Регистрация")
    @Feature("Неудачная регистрация")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("register"), @Tag("smoke")})
    @DisplayName("Проверка регистрации на существующий username")
    void shouldNotRegisterUserWithExistingUsernameTest(TestUser user) {
        LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(user.getUsername(), user.getTestData().password(), user.getTestData().password())
                .errorSubmit();
        loginPage.checkError("Username `" + user.getUsername() + "` already exists");
    }

    @Test
    @Story("Регистрация")
    @Feature("Неудачная регистрация")
    @Severity(CRITICAL)
    @Tags({@Tag("web"), @Tag("register"), @Tag("regress")})
    @DisplayName("Проверка регистрации с разными паролями")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqualTest() {
        String newUsername = randomUsername();
        String password = "12345";

        LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(newUsername, password, "bad password submit")
                .errorSubmit();
        loginPage.checkError("Passwords should be equal");
    }

    @Test
    @Story("Регистрация")
    @Feature("Неудачная регистрация")
    @Severity(CRITICAL)
    @Tags({@Tag("web"), @Tag("register"), @Tag("regress")})
    @DisplayName("Проверка регистрации с паролем 2 символа")
    void shouldShowErrorIfPasswordAndConfirmMinLengthPasswordTest() {
        String newUsername = randomUsername();
        String password = randomPassword(1, 2);

        LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(newUsername, password, password)
                .errorSubmit();
        loginPage.checkError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    @Story("Регистрация")
    @Feature("Неудачная регистрация")
    @Severity(CRITICAL)
    @Tags({@Tag("web"), @Tag("register"), @Tag("regress")})
    @DisplayName("Проверка регистрации с паролем 13 символов")
    void shouldShowErrorIfPasswordAnd5ConfirmMaxLengthPasswordTest() {
        String newUsername = randomUsername();
        String password = randomPassword(13, 14);

        LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
        loginPage.doRegister()
                .fillRegisterPage(newUsername, password, password)
                .errorSubmit();
        loginPage.checkError("Allowed password length should be from 3 to 12 characters");
    }
}
