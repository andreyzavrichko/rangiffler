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
import rangiffler.GetOutcomeInvitationsQuery;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.Token;
import rangiffler.test.gql.BaseGraphQlTest;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("[GraphQL] Outcome invitations")
public class GetOutcomeInvitationsGraphQlTest extends BaseGraphQlTest {

    @Test
    @CreateUser(friends = {
            @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME),
            @Friend(pending = true, friendshipRequestType = Friend.FriendshipRequestType.OUTCOME)
    })
    @ApiLogin
    @Story("Список исходящих заявок")
    @Feature("Друзья")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("invitations"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения списка исходящих заявок")
    void shouldGetOutcomeInvitationsUserListTest(@Token String token) {
        final GetOutcomeInvitationsQuery query = GetOutcomeInvitationsQuery.builder().build();

        final ApolloCall<GetOutcomeInvitationsQuery.Data> queryCall = apolloClient.query(query)
                .addHttpHeader("authorization", token);

        final ApolloResponse<GetOutcomeInvitationsQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        assertNotNull(response.data, "Response data should not be null");
        assertNotNull(response.data.user.outcomeInvitations, "Outcome invitations should be present in the response");
        assertFalse(response.data.user.outcomeInvitations.edges.isEmpty(), "Expected at least one outcome invitation");

        response.data.user.outcomeInvitations.edges.forEach(edge -> {
            assertNotNull(edge.node.id, "Invitation ID should not be null");
            assertNotNull(edge.node.username, "Username should not be null");
            assertNotNull(edge.node.firstname, "First name should not be null");
            assertNotNull(edge.node.surname, "Surname should not be null");
            assertNotNull(edge.node.avatar, "Avatar should not be null");
            assertNotNull(edge.node.location, "Location should not be null");
            assertNotNull(edge.node.friendStatus, "Friend status should not be null");
        });

        assertNotNull(response.data.user.outcomeInvitations.pageInfo, "PageInfo should be present in the response");
        assertNotNull(response.data.user.outcomeInvitations.pageInfo.hasPreviousPage, "hasPreviousPage should not be null");
        assertNotNull(response.data.user.outcomeInvitations.pageInfo.hasNextPage, "hasNextPage should not be null");

    }

}