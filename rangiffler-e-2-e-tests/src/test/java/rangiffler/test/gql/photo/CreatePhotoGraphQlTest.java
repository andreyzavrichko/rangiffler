package rangiffler.test.gql.photo;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.CreatePhotoMutation;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;
import rangiffler.type.CountryInput;
import rangiffler.type.PhotoInput;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[GraphQL] Photo")
public class CreatePhotoGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser
    @ApiLogin
    @Story("Создание фотокарточки")
    @Feature("Фотокарточка")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка успешного создания фотокарточки")
    void shouldCreatePhotoTest(@Token String token) {
        final CreatePhotoMutation mutation = CreatePhotoMutation.builder()
                .input(PhotoInput.builder()
                        .country(CountryInput.builder().code("ru").build())
                        .description("111111")
                        .src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgA")
                        .build())
                .build();

        final ApolloCall<CreatePhotoMutation.Data> mutationCall = apolloClient.mutation(mutation)
                .addHttpHeader("authorization", token);

        final ApolloResponse<CreatePhotoMutation.Data> response = Rx2Apollo.single(mutationCall).blockingGet();

        assertNotNull(response, "Ответ от сервера пустой");
        assertFalse(response.hasErrors(), "Ответ содержит ошибки: " + response.errors);

        CreatePhotoMutation.Data responseData = response.data;
        assertNotNull(responseData, "Данные отсутствуют в ответе");

        CreatePhotoMutation.Photo photo = responseData.photo;
        assertNotNull(photo, "Фото не найдено в ответе");

        assertNotNull(photo.id, "ID фото отсутствует");
        assertNotNull(photo.src, "Источник изображения отсутствует");

        assertNotNull(photo.country, "Данные о стране отсутствуют");
        assertEquals("ru", photo.country.code, "Код страны неверный");
        assertNotNull(photo.country.name, "Название страны отсутствует");
        assertNotNull(photo.country.flag, "Флаг страны отсутствует");

        assertEquals("111111", photo.description, "Описание фото неверное");

        assertNotNull(photo.likes, "Данные о лайках отсутствуют");
        assertEquals(0, photo.likes.total, "Количество лайков неверное");

    }

}