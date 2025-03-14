package rangiffler.test.grpc.userdata;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.model.CountryEntity;
import rangiffler.db.model.UserEntity;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.UserRepository;
import rangiffler.grpc.User;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.testdata.TestUser;

import java.time.Duration;
import java.util.UUID;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("[GRPC] Userdata")
class UpdateUserTest extends GrpcUserdataTestBase {

    @Inject
    private CountryRepository countryRepository;

    @Inject
    private UserRepository userRepository;

    private final Faker faker = new Faker();


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка успешного обновления данных пользователя")
    void shouldUpdateUserDetailsTest(TestUser existingUser) {
        CountryEntity differentCountry = countryRepository.findRequiredCountryByIdNot(existingUser.getCountry().getId());

        User updateRequest = User.newBuilder()
                .setUsername(existingUser.getUsername())
                .setFirstname(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setAvatar(ByteString.copyFrom(new byte[]{}))
                .setCountryId(differentCountry.getId().toString())
                .build();

        User updatedResponse = blockingStub.updateUser(updateRequest);

        assertAll("Валидация ответа обновления",
                () -> assertEquals(existingUser.getId().toString(), updatedResponse.getId(), "ID пользователя не должен изменяться"),
                () -> assertEquals(updateRequest.getUsername(), updatedResponse.getUsername(), "Неверное имя пользователя"),
                () -> assertEquals(updateRequest.getFirstname(), updatedResponse.getFirstname(), "Неверное имя"),
                () -> assertEquals(updateRequest.getLastName(), updatedResponse.getLastName(), "Неверная фамилия"),
                () -> assertArrayEquals(updateRequest.getAvatar().toByteArray(), updatedResponse.getAvatar().toByteArray(), "Аватар не совпадает"),
                () -> assertEquals(updateRequest.getCountryId(), updatedResponse.getCountryId(), "Неверный идентификатор страны")
        );

        UserEntity updatedEntity = Awaitility.await("Ожидание обновления записи в БД")
                .atMost(Duration.ofMillis(10000))
                .pollInterval(Duration.ofMillis(1000))
                .ignoreExceptions()
                .until(() -> userRepository.findRequiredById(existingUser.getId()),
                        entity -> updateRequest.getFirstname().equals(entity.getFirstname())
                );

        assertAll("Проверка данных в БД",
                () -> assertEquals(updatedEntity.getId().toString(), updatedResponse.getId(), "Несовпадение ID"),
                () -> assertEquals(updatedEntity.getUsername(), updatedResponse.getUsername(), "Несовпадение имени пользователя"),
                () -> assertEquals(updatedEntity.getFirstname(), updatedResponse.getFirstname(), "Несовпадение имени"),
                () -> assertEquals(updatedEntity.getLastName(), updatedResponse.getLastName(), "Несовпадение фамилии"),
                () -> assertArrayEquals(updatedEntity.getAvatar(), updatedResponse.getAvatar().toByteArray(), "Несовпадение аватара"),
                () -> assertEquals(updatedEntity.getCountryId().toString(), updatedResponse.getCountryId(), "Несовпадение идентификатора страны")
        );
    }


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка невозможности изменения идентификатора пользователя")
    void shouldNotAllowChangingUserIdTest(TestUser existingUser) {
        User updateRequest = User.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUsername(existingUser.getUsername())
                .setCountryId(existingUser.getCountry().getId().toString())
                .build();

        User response = blockingStub.updateUser(updateRequest);

        assertAll("Проверка неизменности ID",
                () -> assertEquals(existingUser.getId().toString(), response.getId(), "ID пользователя изменился"),
                () -> assertEquals(updateRequest.getUsername(), response.getUsername(), "Имя пользователя не совпадает")
        );

        UserEntity userFromDb = userRepository.findRequiredByUsername(existingUser.getUsername());
        assertAll("Проверка данных в БД",
                () -> assertEquals(userFromDb.getId().toString(), response.getId(), "ID в БД не совпадает"),
                () -> assertEquals(userFromDb.getUsername(), response.getUsername(), "Имя пользователя в БД не совпадает")
        );
    }


    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(CRITICAL)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка невозможности обновления несуществующего пользователя")
    void shouldFailUpdatingNonExistingUserTest() {
        User updateRequest = User.newBuilder()
                .setUsername(faker.name().username())
                .build();

        StatusRuntimeException thrownException = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.updateUser(updateRequest),
                "Ожидается ошибка при обновлении несуществующего пользователя"
        );
        assertTrue(thrownException.getMessage().contains(updateRequest.getUsername()),
                "Сообщение об ошибке должно содержать имя пользователя");
    }
}
