package rangiffler.test.gql.userdata;

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
import rangiffler.GetUserQuery;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("[GraphQL] Userdata")
public class GetUserGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser
    @ApiLogin
    @Story("Получение информации пользователя")
    @Feature("Пользователь")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("user"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения информации о пользователе")
    void shouldGetUserTest(@Token String token) {
        final GetUserQuery query = GetUserQuery.builder().build();

        final ApolloCall<GetUserQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", token);

        final ApolloResponse<GetUserQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        assertNotNull(response, "Ответ от сервера пустой");
        assertFalse(response.hasErrors(), "Ответ содержит ошибки: " + response.errors);

        GetUserQuery.Data responseData = response.data;
        assertNotNull(responseData, "Данные отсутствуют в ответе");

        GetUserQuery.User user = responseData.user;
        assertNotNull(user, "Пользователь не найден в ответе");

        assertNotNull(user.id, "ID пользователя отсутствует");
        assertNotNull(user.username, "Username отсутствует");
        assertNotNull(user.firstname, "Имя отсутствует");
        assertNotNull(user.surname, "Фамилия отсутствует");
        assertNotNull(user.avatar, "Аватар отсутствует");
    }

}