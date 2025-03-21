package rangiffler.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import lombok.SneakyThrows;
import rangiffler.api.AuthApi;
import rangiffler.api.core.RestClient;
import rangiffler.api.core.ThreadSafeCookieStore;
import rangiffler.config.Config;
import rangiffler.utils.OAuthUtils;
import retrofit2.Response;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.nio.charset.StandardCharsets;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;

public class AuthApiClient extends RestClient {
    private static final Config CFG = Config.getInstance();
    private static final String CLIENT_ID = "client";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String REDIRECT_URI = CFG.frontUrl() + "/authorized";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, ScalarsConverterFactory.create(), HEADERS);
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String doLogin(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

        authorize(codeChallenge);
        String code = login(username, password);
        return token(code, codeVerifier);
    }

    @SneakyThrows
    private void authorize(String codeChallenge) {
        authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD
        ).execute();
    }

    @SneakyThrows
    private String login(String username, String password) {
        Response<String> loginResponse = authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        String url = loginResponse.raw().request().url().toString();
        return StringUtils.substringAfter(url, "code=");
    }

    @SneakyThrows
    private String token(String code, String codeVerifier) {
        Response<String> tokenResponse = authApi.token(
                CLIENT_ID,
                REDIRECT_URI,
                GRANT_TYPE,
                code,
                codeVerifier
        ).execute();

        assert tokenResponse.body() != null;
        return new ObjectMapper().readTree(
                tokenResponse.body().getBytes(StandardCharsets.UTF_8)
        ).get("id_token").asText();
    }

    @SneakyThrows
    public Response<Void> register(String username, String password, String confirmPassword) {
        // Получение CSRF-токена
        authApi.requestRegisterForm().execute();
        String csrfToken = ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN");
        // Отправка данных на регистрацию
        Response<Void> response = authApi.register(username, password, confirmPassword, csrfToken).execute();
        return response;
    }
}