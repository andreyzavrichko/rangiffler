package rangiffler.test.gql.geo;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import org.junit.jupiter.api.Test;
import rangiffler.GetCountriesQuery;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AllCountryGraphQlTest extends BaseGraphQlTest {

    @Test
    void allCountriesQueryShouldReturnValidCountries() {
        // Создание GraphQL-запроса
        final GetCountriesQuery query = GetCountriesQuery.builder().build();

        // Выполнение запроса с добавлением заголовка авторизации
        final ApolloCall<GetCountriesQuery.Data> queryCall = apolloClient.query(query);
        //   .addHttpHeader("authorization", token);

        final ApolloResponse<GetCountriesQuery.Data> response = Rx2Apollo.single(queryCall).blockingGet();


        // Извлечение данных стран из ответа
        List<GetCountriesQuery.Country> countries = response.dataOrThrow().countries;
        assertFalse(countries.isEmpty(), "Countries list should not be empty");

        // Проверка данных каждой страны
        for (GetCountriesQuery.Country country : countries) {
            // Проверка, что поля code и name не пустые
            assertFalse(country.code.isEmpty(), "Country code should not be empty");
            assertFalse(country.name.isEmpty(), "Country name should not be empty");

            // Проверка, что поле flag не пустое и соответствует маске изображения
            assertTrue(isValidFlag(country.flag), "Country flag should match the expected format");
        }
    }

    // Метод для проверки маски flag
    private boolean isValidFlag(String flag) {
        // Регулярное выражение для проверки, что flag начинается с data:image/png;base64,
        String flagPattern = "^data:image/png;base64,.*";
        return Pattern.matches(flagPattern, flag);
    }


}