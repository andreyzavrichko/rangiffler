package rangiffler.data.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import rangiffler.config.Config;
import rangiffler.data.entity.userdata.UserEntity;
import rangiffler.data.repository.UserdataUserRepository;


import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Optional;
import java.util.UUID;

import static rangiffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Nonnull
    @Override
    public UserEntity create(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    @Nonnull
    @Override
    public UserEntity update(UserEntity user) {
        return entityManager.merge(user);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM UserdataEntity u WHERE u.username = :username", UserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(UserEntity user) {
        UserEntity managedUser = entityManager.contains(user) ? user : entityManager.merge(user);
        entityManager.remove(managedUser);
        entityManager.flush();
    }


}
