package rangiffler.jupiter.extension;

import com.github.javafaker.Faker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import rangiffler.jupiter.annotation.CreateExtrasUsers;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Extras;
import rangiffler.model.testdata.TestUser;
import rangiffler.service.UserService;
import rangiffler.service.impl.UserDbService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CreateExtrasUserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE
      = ExtensionContext.Namespace.create(CreateExtrasUserExtension.class);
  private final UserService userService = new UserDbService();
  private final Faker faker = new Faker();

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    var usersParameters = extractUsersForTest(extensionContext);
    var createdUsers = usersParameters.stream()
        .map(userParameters -> userService.createRandomUser())
        .collect(Collectors.toList());
    extensionContext.getStore(NAMESPACE).put(extensionContext.getUniqueId(), createdUsers);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void afterEach(ExtensionContext extensionContext) {
    List<TestUser> createdUsers = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class);
    for (var createdUser : createdUsers) {
      userService.deleteUser(createdUser.getUsername());
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return (parameterContext.getParameter().getType().isAssignableFrom(TestUser[].class)
        && extensionContext.getRequiredTestMethod().isAnnotationPresent(CreateExtrasUsers.class)
        && parameterContext.getParameter().isAnnotationPresent(Extras.class));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class)
        .toArray(TestUser[]::new);
  }

  private List<CreateUser> extractUsersForTest(ExtensionContext context) {
    List<CreateUser> users = new ArrayList<>();
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), CreateExtrasUsers.class).ifPresent(
        createUsers -> users.addAll(Arrays.asList(createUsers.value()))
    );
    return users;
  }
}
