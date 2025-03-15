package rangiffler.service.impl;

import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import rangiffler.config.Config;
import rangiffler.data.entity.auth.AuthUserEntity;
import rangiffler.data.entity.auth.Authority;
import rangiffler.data.entity.auth.AuthorityEntity;
import rangiffler.data.entity.userdata.UserEntity;
import rangiffler.data.repository.AuthUserRepository;
import rangiffler.data.repository.UserdataUserRepository;
import rangiffler.data.repository.impl.AuthUserRepositoryHibernate;
import rangiffler.data.repository.impl.UserdataUserRepositoryHibernate;
import rangiffler.data.tpl.XaTransactionTemplate;

import rangiffler.model.api.TestData;
import rangiffler.model.api.UserJson;

import rangiffler.service.UsersClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Step("Create user using SQL")
    @Nonnull
    @Override
    public UserJson createUser(String username, String password) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> UserJson.fromEntity(
                                createNewUser(username, password)
                        ).addTestData(new TestData(password))
                )
        );
    }



    @Nonnull
    private UserEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);

        return userdataUserRepository.create(userdataEntity(username, "4cc91f80-f195-11ee-9b32-0242ac110002"));
    }

    @Step("Delete user using SQL")
    @Override
    public void deleteUserByUsername(String username) {
        xaTransactionTemplate.execute(() -> {
            userdataUserRepository.findByUsername(username).ifPresent(userdataUserRepository::delete);
            authUserRepository.findByUsername(username).ifPresent(authUserRepository::delete);
            return null;
        });
    }

    @Nonnull
    @Override
    @Step("Find user by username")
    public Optional<UserJson> findUserByUsername(String username) {
        return userdataUserRepository.findByUsername(username)
                .map(UserJson::fromEntity);
    }



    @Nonnull
    private UserEntity userdataEntity(String username, String country_id ) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCountryId(country_id);
        return ue;
    }

    @Nonnull
    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }


}
