package rangiffler.test.gql.geo;

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
import rangiffler.GetCountriesQuery;
import rangiffler.test.gql.BaseGraphQlTest;

import java.util.List;
import java.util.regex.Pattern;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[GraphQL] Geo")
public class AllCountryGraphQlTest extends BaseGraphQlTest {

    @Test
    @Story("Список стран")
    @Feature("Гео")
    @Severity(BLOCKER)
    @Tags({@Tag("graphql"), @Tag("geo"), @Tag("smoke")})
    @DisplayName("[GraphQL] Проверка получения списка всех стран")
    void shouldAllCountriesQueryShouldReturnValidCountriesTest() {
        final GetCountriesQuery query = GetCountriesQuery.builder().build();

        final ApolloCall<GetCountriesQuery.Data> queryCall = apolloClient.query(query);

        final ApolloResponse<GetCountriesQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();

        List<GetCountriesQuery.Country> countries = response.dataOrThrow().countries;
        assertFalse(countries.isEmpty(), "Countries list should not be empty");

        for (GetCountriesQuery.Country country : countries) {
            assertFalse(country.code.isEmpty(), "Country code should not be empty");
            assertFalse(country.name.isEmpty(), "Country name should not be empty");

            assertTrue(isValidFlag(country.flag), "Country flag should match the expected format");
        }
    }

    private boolean isValidFlag(String flag) {
        String flagPattern = "^data:image/png;base64,.*";
        return Pattern.matches(flagPattern, flag);
    }


}