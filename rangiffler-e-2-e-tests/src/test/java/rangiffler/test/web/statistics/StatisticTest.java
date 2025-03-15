package rangiffler.test.web.statistics;

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
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.page.MyTravelsPage;
import rangiffler.utils.ScreenDiffResult;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
@DisplayName("[WEB] Карта")
class StatisticTest {

    @Inject
    private MyTravelsPage myTravelsPage;


    @ApiLogin
    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA, likes = 2)
            }
    )
    @Story("Путешествия")
    @Feature("Добавление фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("map"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка добавления фотокарточки на карту")
    @ScreenShotTest(value = "image/expected_map.png")
    void userPhotoTest(BufferedImage expected) throws IOException {
        myTravelsPage.open();

        assertFalse(new ScreenDiffResult(
                expected,
                myTravelsPage.worldMapScreenshot()
        ), "Скриншоты отличаются! См. разницу в Allure отчете");
    }



    }



