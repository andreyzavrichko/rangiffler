package rangiffler.test.gql.geo;

import com.apollographql.adapter.core.DateAdapter;
import com.apollographql.java.client.ApolloClient;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.extension.RegisterExtension;
import rangiffler.config.Config;
import rangiffler.jupiter.annotation.meta.GqlTest;
import rangiffler.jupiter.extension.ApiLoginExtension;
import rangiffler.type.Date;

@GqlTest
public class BaseGraphQlTest {
    protected static final Config CFG = Config.getInstance();
    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    protected static final ApolloClient apolloClient = new ApolloClient.Builder()
            .serverUrl(CFG.gatewayUrl() + "/graphql")
            .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
            .okHttpClient(
                    new OkHttpClient.Builder()
                            .addNetworkInterceptor(new AllureOkHttp3())
                            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build()
            ).build();
}
