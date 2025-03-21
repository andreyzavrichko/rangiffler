package rangiffler.service.impl;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import rangiffler.db.model.*;
import rangiffler.db.repository.*;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.mapper.CountryMapper;
import rangiffler.mapper.UserEntityMapper;
import rangiffler.model.testdata.*;
import rangiffler.service.PhotoService;
import rangiffler.service.UserService;
import rangiffler.utils.ImageUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class UserDbService implements UserService {

    private final UserRepository userRepository = new UserRepositoryJdbc();
    private final FriendshipRepository friendshipRepository = new FriendshipRepositoryJdbc();
    private final CountryRepository countryRepository = new CountryRepositoryJdbc();
    private final PhotoService photoService = new PhotoDbService();
    private final Faker faker = new Faker();

    @Override
    public TestUser createUser(String username, String password, String firstname, String lastname, UUID countryId,
                               byte[] avatar) {
        var userAuth = new UserAuthEntity();
        userAuth.setUsername(username);
        userAuth.setPassword(password);
        userAuth.setEnabled(true);
        userAuth.setAccountNonExpired(true);
        userAuth.setAccountNonLocked(true);
        userAuth.setCredentialsNonExpired(true);
        var authorities = Arrays.stream(Authority.values()).map(
                a -> {
                    var ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }
        ).toList();

        userAuth.setAuthorities(authorities);

        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setLastName(lastname);
        userEntity.setFirstname(firstname);
        userEntity.setCountryId(countryId);
        userEntity.setAvatar(avatar);

        userRepository.createInAuth(userAuth);
        userEntity = userRepository.createInUserdata(userEntity);
        log.info("Создан пользователь с id {}", userEntity.getId());
        log.info("Создан пользователь с username {}", userEntity.getUsername());

        var testUser = UserEntityMapper.toUser(userEntity);
        testUser.setTestData(new TestData(password));
        return testUser;
    }

    @Override
    public void deleteUser(TestUser testUser) {
        testUser.getPhotos().forEach(s -> photoService.deletePhoto(s.getId()));
        testUser.getPhotos().stream()
                .flatMap(s -> s.getLikes().stream())
                .forEach(s -> {
                    var userId = s.getUserId();
                    var username = userRepository.findRequiredById(userId).getUsername();
                    deleteUser(username);
                });
        deleteUser(testUser.getUsername());
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteInAuthByUsername(username);
        userRepository.deleteInUserdataByUsername(username);
    }

    @Override
    public TestUser createRandomUser() {
        var userCountry = countryRepository.findRequiredCountryByCode(CountryCodes.US.getCode());
        var createdUser = createUser(faker.name().username(), "12345",
                faker.random().hex(8),
                faker.random().hex(8),
                userCountry.getId(),
                ImageUtils.getImageFromResourceAsBase64(UserAvatars.DEFAULT.getFileName()).getBytes(StandardCharsets.UTF_8)
        );
        createdUser.setCountry(CountryMapper.toTestCountry(userCountry));
        return createdUser;
    }

    @Override
    public void createFriendship(UUID firstFriendId, UUID secondFriendId, Boolean isPending) {
        friendshipRepository.createFriendship(
                firstFriendId,
                secondFriendId,
                LocalDateTime.now(),
                isPending ? FriendshipStatus.PENDING : FriendshipStatus.ACCEPTED
        );
    }

    @Override
    public TestUser createTestUser(CreateUser userParameters) {
        var username = userParameters.username().isEmpty() ? faker.name().username() : userParameters.username();
        var password = userParameters.password().isEmpty() ? "12345" : userParameters.password();
        var userCountry = countryRepository.findRequiredCountryByCode(userParameters.countryCode().getCode());
        var userAvatar = ImageUtils.getImageFromResourceAsBase64(userParameters.avatar().getFileName());

        var createdUser = createUser(username, password, faker.random().hex(8), faker.random().hex(8),
                userCountry.getId(), userAvatar.getBytes(StandardCharsets.UTF_8));
        createdUser.setCountry(CountryMapper.toTestCountry(userCountry));

        createdUser.getPhotos().addAll(createPhotos(createdUser.getId(), userParameters.photos()));

        for (var friendParameters : userParameters.friends()) {
            var createdFriend = createRandomUser();
            createdFriend.getPhotos().addAll(createPhotos(createdFriend.getId(), friendParameters.photos()));

            if (!friendParameters.pending()) {
                createFriendship(createdUser.getId(), createdFriend.getId(), false);
                createdUser.getFriends().add(createdFriend);
            } else {
                if (friendParameters.friendshipRequestType() == Friend.FriendshipRequestType.OUTCOME) {
                    createFriendship(createdUser.getId(), createdFriend.getId(), true);
                    createdUser.getOutcomeInvitations().add(createdFriend);
                } else {
                    createFriendship(createdFriend.getId(), createdUser.getId(), true);
                    createdUser.getIncomeInvitations().add(createdFriend);
                }
            }
        }

        return createdUser;
    }

    @Override
    public List<TestPhoto> createPhotos(UUID userId, WithPhoto[] photosParameters) {
        var createdPhotos = new ArrayList<TestPhoto>();
        for (var photoParameters : photosParameters) {
            var photoCountry = countryRepository.findRequiredCountryByCode(photoParameters.countryCode().getCode());
            var photoDescription = photoParameters.description().isBlank()
                    ? faker.internet().uuid()
                    : photoParameters.description();
            var createdPhoto = photoService.createPhoto(userId,
                    photoParameters.countryCode().getCode(),
                    photoParameters.image().getFileName(),
                    photoDescription
            );
            createdPhoto.setCountry(CountryMapper.toTestCountry(photoCountry));

            var likes = new ArrayList<TestLike>();
            for (var i = 0; i < photoParameters.likes(); i++) {
                var likeUser = createRandomUser();
                photoService.likePhoto(likeUser.getId(), createdPhoto.getId());
                likes.add(new TestLike(null, likeUser.getId(), null));
            }
            createdPhoto.setLikes(likes);
            createdPhotos.add(createdPhoto);
        }
        return createdPhotos;
    }
}
