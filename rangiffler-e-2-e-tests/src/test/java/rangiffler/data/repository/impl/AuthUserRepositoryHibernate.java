package rangiffler.data.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import rangiffler.config.Config;
import rangiffler.data.entity.auth.AuthUserEntity;
import rangiffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static rangiffler.data.jpa.EntityManagers.em;


@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM AuthUserEntity u WHERE u.username = :username", AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(AuthUserEntity user) {
        AuthUserEntity managedUser = entityManager.contains(user) ? user : entityManager.merge(user);
        entityManager.remove(managedUser);
        entityManager.flush();
    }
}
