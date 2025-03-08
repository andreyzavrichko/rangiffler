package rangiffler.test.web.statistics;

import com.google.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.page.MyTravelsPage;

@WebTest
class StatisticTest {
// Todo переписать на скриншот тестирование


//    @Inject
//    private MyTravelsPage myTravelsPage;
//
//    @DisplayName("[web] Получение статистики фото пользователя")
//    @ApiLogin
//    @CreateUser(
//            photos = {
//                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE, likes = 2)
//            }
//    )
//    @Test
//    void userPhotoTest() {
//        myTravelsPage.open()
//               .statisticShouldBePresented(CountryCodes.CN.getCode(), "China", 1);
//    }
//
//    @DisplayName("[web] Получение статистики фото пользователя и его друзей")
//    @ApiLogin
//    @CreateUser(
//            photos = {
//                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE),
//                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.AMSTERDAM)
//            }, friends = {
//            @Friend(photos = @WithPhoto(countryCode = CountryCodes.MX, image = PhotoFiles.AMSTERDAM)),
//            @Friend(pending = true, photos = @WithPhoto(countryCode = CountryCodes.CA, image = PhotoFiles.AMSTERDAM)),
//            @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME,
//                    photos = @WithPhoto(countryCode = CountryCodes.CA, image = PhotoFiles.AMSTERDAM)
//            )
//    }
//    )
//    @Test
//    void userPhotoWithFriendsTest() {
//        myTravelsPage.open()
//                .clickWithFriendsButton()
//                .statisticShouldBePresented(CountryCodes.CN.getCode(), "China", 2)
//                .statisticShouldBePresented(CountryCodes.MX.getCode(), "Mexico", 1);
//    }
}
