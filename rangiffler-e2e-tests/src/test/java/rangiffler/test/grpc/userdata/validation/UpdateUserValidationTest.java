package rangiffler.test.grpc.userdata.validation;

import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.User;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.userdata.GrpcUserdataTestBase;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("[GRPC] Userdata")
class UpdateUserValidationTest extends GrpcUserdataTestBase {


    @CreateUser
    @Test
    @Story("Обновление пользователя")
    @Feature("Пользователи")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("userdata"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка обновления пользователя с пустым именем пользователя")
    void shouldFailUpdatingWithEmptyUsername(TestUser existingUser) {
        User updateRequest = User.newBuilder()
                .setUsername("")
                .setFirstname(existingUser.getFirstname())
                .setLastName(existingUser.getLastName())
                .setCountryId(existingUser.getCountry().getId().toString())
                .build();

        assertThrows(StatusRuntimeException.class,
                () -> blockingStub.updateUser(updateRequest),
                "Ожидается ошибка при обновлении пользователя с пустым username"
        );

    }


}
