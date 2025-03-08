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
import rangiffler.model.testdata.TestUser;
import rangiffler.page.MyProfilePage;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@WebTest
@DisplayName("Информация о пользователе")
class CurrentUserInfoTest {

    @Inject
    private MyProfilePage myProfilePage;


    @ApiLogin
    @CreateUser
    @Test
    @Story("Путешествия")
    @Feature("Изменение фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("user"), @Tag("smoke")})
    @DisplayName("Проверка отображения данных пользователя")
    void shouldCurrentUserInfoTest(TestUser user) {
        myProfilePage.open()
                .pageHeaderShouldBeVisible()
                .usernameShouldBe(user.getUsername())
                .firstnameShouldBe(user.getFirstname())
                .lastnameShouldBe(user.getLastName())
                .locationNameShouldBe(user.getCountry().getName())
                .locationFlagShouldBe(user.getCountry().getFlag())
                .avatarShouldBe(user.getAvatar());
    }
}
