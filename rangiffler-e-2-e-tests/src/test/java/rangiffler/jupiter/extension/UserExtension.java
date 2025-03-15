package rangiffler.jupiter.extension;


import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.api.UserJson;
import rangiffler.model.testdata.TestUser;
import rangiffler.service.UserService;
import rangiffler.service.impl.UserDbService;


public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UserExtension.class);
    private final UserService userService = new UserDbService();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        var userParameters = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                CreateUser.class
        );

        if (userParameters.isPresent()) {
            var createdUser = userService.createTestUser(userParameters.get());
            extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdUser);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        var createdUser = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), TestUser.class);
        if (createdUser != null) {
            createdUser.getFriends().forEach(userService::deleteUser);
            createdUser.getIncomeInvitations().forEach(userService::deleteUser);
            createdUser.getOutcomeInvitations().forEach(userService::deleteUser);
            userService.deleteUser(createdUser);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(TestUser.class)
                && extensionContext.getRequiredTestMethod().isAnnotationPresent(CreateUser.class);
    }

    @Override
    public TestUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), TestUser.class);
    }

    public static TestUser getUserJson() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), TestUser.class);
    }
}
