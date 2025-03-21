package rangiffler.service;

import rangiffler.model.api.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface UsersClient {
    @Nonnull
    UserJson createUser(String username, String password);

    void deleteUserByUsername(String username);

    @Nonnull
    Optional<UserJson> findUserByUsername(String username);
}
