package rangiffler.data.repository;



import rangiffler.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {

    @Nonnull
    AuthUserEntity create(AuthUserEntity user);

    @Nonnull
    Optional<AuthUserEntity> findById(UUID id);

    @Nonnull
    Optional<AuthUserEntity> findByUsername(String username);

    void delete(AuthUserEntity user);
}
