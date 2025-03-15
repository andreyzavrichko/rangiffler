package rangiffler.test.kafka;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import rangiffler.api.AuthApi;
import rangiffler.api.core.RestClient;
import rangiffler.api.core.ThreadSafeCookieStore;
import rangiffler.config.Config;
import rangiffler.jupiter.annotation.meta.KafkaTest;
import rangiffler.model.api.UserJson;
import rangiffler.service.impl.KafkaService;
import rangiffler.utils.RandomDataUtils;

import static io.qameta.allure.SeverityLevel.*;

@KafkaTest
@DisplayName("Kafka")
public class AuthKafkaTest {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);

    @Test
    @Story("Отправка пользователя в очередь")
    @Feature("Регистрация")
    @Severity(BLOCKER)
    @Tags({@Tag("kafka"), @Tag("auth"), @Tag("smoke")})
    @DisplayName("[Kafka] Проверка отправки пользователя в очередь")
    void userShouldBeProducedToKafkaTest() throws Exception {
        final String username = RandomDataUtils.randomUsername();
        final String password = "12345";

        authApi.requestRegisterForm().execute();
        authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        UserJson userFromKafka = KafkaService.getUser(username);
        Assertions.assertEquals(
                username,
                userFromKafka.username()
        );
    }

    @Test
    @Story("Отправка пользователя в очередь")
    @Feature("Регистрация")
    @Severity(CRITICAL)
    @Tags({@Tag("kafka"), @Tag("auth"), @Tag("smoke")})
    @DisplayName("[Kafka] Проверка отсутствия незарегистрированного пользователя")
    void nonExistentUserShouldNotBeProducedToKafkaTest() throws Exception {
        String nonExistentUser = "randomUser123";

        UserJson userFromKafka = KafkaService.getUser(nonExistentUser);

        Assertions.assertNull(userFromKafka, "Kafka вернула пользователя, которого не должно существовать");
    }

    @Test
    @Story("Отправка пользователя в очередь")
    @Feature("Регистрация")
    @Severity(NORMAL)
    @Tags({@Tag("kafka"), @Tag("auth"), @Tag("smoke")})
    @DisplayName("[Kafka] Проверка отсутствия пользователя в пустым username")
    void invalidUserShouldNotBeProducedToKafkaTest() throws Exception {
        final String username = "";
        final String password = "12345";

        authApi.requestRegisterForm().execute();
        authApi.register(username, password, password, ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")).execute();

        UserJson userFromKafka = KafkaService.getUser(username);

        Assertions.assertNull(userFromKafka, "Kafka не должна была сохранить пользователя с пустым именем");
    }
}