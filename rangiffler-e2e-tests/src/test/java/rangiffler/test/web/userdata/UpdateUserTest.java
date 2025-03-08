package rangiffler.test.web.userdata;

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
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.page.MyProfilePage;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static io.qameta.allure.SeverityLevel.NORMAL;
import static rangiffler.utils.RandomDataUtils.*;


@WebTest
@DisplayName("Обновление пользователя")
class UpdateUserTest {

    @Inject
    private MyProfilePage myProfilePage;


    @CreateUser
    @ApiLogin
    @Test
    @Story("Профиль")
    @Feature("Изменение профиля")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("user"), @Tag("smoke")})
    @DisplayName("Проверка успешного обновления пользователя")
    void shouldUpdateUserTest() {
        myProfilePage.open()
                .setFirstname(randomName())
                .setLastname(randomLastname())
                .setLocation(CountryCodes.CN.getCode())
                .setAvatar("image/defaultAvatar.png")
                .saveChanges()
                .checkSuccessTitle("Your profile is successfully updated");
    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Профиль")
    @Feature("Изменение профиля")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("user"), @Tag("regress")})
    @DisplayName("Обновление пользователя - FirstName 51 символ")
    void shouldUpdateUserWithFirstName51Test() {
        myProfilePage.open()
                .setFirstname(randomPassword(51, 52))
                .setLastname(randomLastname())
                .setLocation(CountryCodes.CN.getCode())
                .setAvatar("image/defaultAvatar.png")
                .saveChanges()
                .checkErrorFirstnameTitle("First name length has to be not longer that 50 symbols");

    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Профиль")
    @Feature("Изменение профиля")
    @Severity(NORMAL)
    @Tags({@Tag("web"), @Tag("user"), @Tag("regress")})
    @DisplayName("Обновление пользователя - Password 101 символ")
    void shouldUpdateUserWithPassword101Test() {
        myProfilePage.open()
                .setFirstname(randomUsername())
                .setLastname(randomPassword(101, 102))
                .setLocation(CountryCodes.CN.getCode())
                .setAvatar("image/defaultAvatar.png")
                .saveChanges()
                .checkErrorLastnameTitle("Surname length has to be not longer that 100 symbols");

    }


}
