package rangiffler.test.gql.people;

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
import rangiffler.GetPeopleQuery;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("[GraphQL] People")
public class GetPeopleGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser(friends = {
            @Friend
    })
    @ApiLogin
    @Story("Список всех людей")
    @Feature("Люди")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("people"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения списка всех людей")
    void shouldGetPeopleUserListTest(@Token String token) {
        final GetPeopleQuery query = GetPeopleQuery.builder().build();

        final ApolloCall<GetPeopleQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", token);

        final ApolloResponse<GetPeopleQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        assertNotNull(response, "Response should not be null");
        assertFalse(response.hasErrors(), "Response should not contain errors");
        assertNotNull(response.data, "Response data should not be null");


    }

}