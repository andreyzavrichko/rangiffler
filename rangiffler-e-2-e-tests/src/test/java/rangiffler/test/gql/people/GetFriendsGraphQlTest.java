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
import rangiffler.GetFriendsQuery;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;

import java.util.List;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[GraphQL] Friends")
public class GetFriendsGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser( friends = {
        @Friend,
        @Friend
    })
    @ApiLogin
    @Story("Список друзей")
    @Feature("Друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("friends"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения списка друзей")
    void shouldGetFriendsListUserTest(@Token String token) {
         final GetFriendsQuery query = GetFriendsQuery.builder().build();

        final ApolloCall<GetFriendsQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", token);

        final ApolloResponse<GetFriendsQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        assertNotNull(response, "Ответ от сервера пустой");
        assertFalse(response.hasErrors(), "Ответ содержит ошибки: " + response.errors);

        assert response.data != null;
        List<GetFriendsQuery.Edge> friends = response.data.user.friends.edges;

        assertEquals(2, friends.size(), "Ожидалось 2 друга, но получено " + friends.size());

        for (GetFriendsQuery.Edge edge : friends) {
            GetFriendsQuery.Node friend = edge.node;

            assertNotNull(friend.id, "ID друга не должно быть пустым");
            assertNotNull(friend.username, "Username друга не должен быть пустым");
            assertNotNull(friend.firstname, "Firstname друга не должен быть пустым");
            assertNotNull(friend.surname, "Surname друга не должен быть пустым");
            assertNotNull(friend.avatar, "Avatar друга не должен быть пустым");

            assertNotNull(friend.location, "Location друга не должна быть пустой");
            assertNotNull(friend.location.code, "Location code друга не должен быть пустым");
            assertNotNull(friend.location.name, "Location name друга не должен быть пустым");
        }

    }

}