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
import rangiffler.GetInvitationsQuery;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[GraphQL] Invitations")
public class GetInvitationsGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser(friends = {
            @Friend(pending = true),
            @Friend(pending = true)
    })
    @ApiLogin
    @Story("Список входящих заявок")
    @Feature("Друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("invitations"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения списка входящих заявок")
    void shouldGetInvitationsUserListTest(@Token String token) {
        GetInvitationsQuery query = GetInvitationsQuery.builder()
                .page(0)
                .size(10)
                .searchQuery("")
                .build();

        ApolloCall<GetInvitationsQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", token);

        ApolloResponse<GetInvitationsQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        assertNotNull(response.data, "Response data should not be null");
        assertNotNull(response.data.user, "User data should not be null");
        assertNotNull(response.data.user.incomeInvitations, "Income invitations should not be null");
        assertNotNull(response.data.user.incomeInvitations.edges, "Edges of income invitations should not be null");

        assertFalse(response.data.user.incomeInvitations.edges.isEmpty(), "Income invitations list should not be empty");

        assertTrue(response.data.user.incomeInvitations.edges.size() <= 10, "The number of invitations should not exceed the requested size");

        for (GetInvitationsQuery.Edge edge : response.data.user.incomeInvitations.edges) {
            assertNotNull(edge.node, "Invitation node should not be null");
            assertNotNull(edge.node.id, "Invitation ID should not be null");
            assertNotNull(edge.node.friendStatus, "Invitation status should not be null");
        }

    }

}