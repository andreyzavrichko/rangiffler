package rangiffler.jupiter.extension;


import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import rangiffler.api.core.ThreadSafeCookieStore;
import rangiffler.config.Config;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Token;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.MainPage;
import rangiffler.service.impl.AuthApiClient;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    public ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        var apiLoginAnnotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                ApiLogin.class

        );

        var createUserAnnotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                CreateUser.class
        ).orElse(null);

        if (apiLoginAnnotation.isPresent()) {
            final TestUser userFromUserExtension = UserExtension.getUserJson();
            var token = authApiClient.doLogin(userFromUserExtension.getUsername(), userFromUserExtension.getTestData().password());
            setToken(token);
        }

        // Настройка браузера
        if (setupBrowser) {
            Selenide.open(CFG.frontUrl());
            Selenide.localStorage().setItem("id_token", getToken());
            WebDriverRunner.getWebDriver().manage().addCookie(
                    getJsessionIdCookie()
            );
            Selenide.open(MainPage.URL, MainPage.class).checkThatHeaderPageLoaded();
        }


    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return "Bearer " + getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static void setCodeVerifier(String codeVerifier) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code_verifier", codeVerifier);
    }

    public static String getCodeVerifier() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code_verifier", String.class);
    }

    public static void setCodeChallenge(String codeChallenge) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code_challenge", codeChallenge);
    }

    public static String getCodeChallenge() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code_challenge", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
