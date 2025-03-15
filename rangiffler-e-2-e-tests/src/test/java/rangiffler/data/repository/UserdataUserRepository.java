package rangiffler.data.repository;

import rangiffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {

    @Nonnull
    UserEntity create(UserEntity user);

    @Nonnull
    UserEntity update(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);


}
