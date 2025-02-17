package rangiffler.service;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rangiffler.data.FriendshipEntity;
import rangiffler.data.FriendshipStatus;
import rangiffler.data.UserEntity;
import rangiffler.data.repository.FriendshipRepository;
import rangiffler.data.repository.UserRepository;
import rangiffler.grpc.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private UserDataService userDataService;

    private UserEntity testUser;
    private UserEntity friendUser;
    private final UUID testUserId = UUID.randomUUID();
    private final UUID friendUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = createTestUser(testUserId, "testuser");
        friendUser = createTestUser(friendUserId, "frienduser");
    }

    @Test
    void getUserByUsernameShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        TestStreamObserver<User> responseObserver = new TestStreamObserver<>();
        userDataService.getUser(
                UserRequest.newBuilder().setUsername("testuser").build(),
                responseObserver
        );

        User response = responseObserver.getResponse();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getId()).isEqualTo(testUserId.toString());
    }


    @Test
    void updateUserFriendshipAddShouldCreatePendingRequest() {
        when(userRepository.findById(UUID.fromString(testUserId.toString()))).thenReturn(Optional.of(testUser));
        when(userRepository.findById(UUID.fromString(friendUserId.toString()))).thenReturn(Optional.of(friendUser));

        TestStreamObserver<User> responseObserver = new TestStreamObserver<>();
        userDataService.updateUserFriendship(
                UpdateUserFriendshipRequest.newBuilder()
                        .setActionAuthorUserId(testUserId.toString())
                        .setActionTargetUserId(friendUserId.toString())
                        .setAction(FriendshipAction.ADD)
                        .build(),
                responseObserver
        );

        ArgumentCaptor<FriendshipEntity> captor = ArgumentCaptor.forClass(FriendshipEntity.class);
        verify(friendshipRepository).saveAndFlush(captor.capture());

        FriendshipEntity savedFriendship = captor.getValue();
        assertThat(savedFriendship.getRequester()).isEqualTo(testUser);
        assertThat(savedFriendship.getAddressee()).isEqualTo(friendUser);
        assertThat(savedFriendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
    }

    @Test
    void updateUserFriendshipAcceptShouldUpdateStatus() {
        FriendshipEntity friendship = createFriendship(friendUser, testUser, FriendshipStatus.PENDING);
        when(userRepository.findById(UUID.fromString(testUserId.toString()))).thenReturn(Optional.of(testUser));
        when(userRepository.findById(UUID.fromString(friendUserId.toString()))).thenReturn(Optional.of(friendUser));
        when(friendshipRepository.findByRequesterAndAddresseeAndStatus(
                eq(friendUser), eq(testUser), eq(FriendshipStatus.PENDING)
        )).thenReturn(Optional.of(friendship));

        TestStreamObserver<User> responseObserver = new TestStreamObserver<>();
        userDataService.updateUserFriendship(
                UpdateUserFriendshipRequest.newBuilder()
                        .setActionAuthorUserId(testUserId.toString())
                        .setActionTargetUserId(friendUserId.toString())
                        .setAction(FriendshipAction.ACCEPT)
                        .build(),
                responseObserver
        );

        assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
        assertThat(responseObserver.getResponse().getFriendStatus()).isEqualTo(FriendStatus.FRIEND);
    }

    @Test
    void getAllUsersShouldReturnFilteredResults() {
        Page<UserEntity> userPage = new PageImpl<>(List.of(friendUser));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsernameNotAndSearchQuery(
                eq("testuser"),
                any(PageRequest.class),
                eq("query")
        )).thenReturn(userPage);
        when(friendshipRepository.findFriendships(anyList(), eq(testUser))).thenReturn(List.of());

        TestStreamObserver<AllUsersResponse> responseObserver = new TestStreamObserver<>();
        userDataService.getAllUsers(
                AllUsersRequest.newBuilder()
                        .setUsername("testuser")
                        .setSearchQuery("query")
                        .setPage(0)
                        .setSize(10)
                        .build(),
                responseObserver
        );

        AllUsersResponse response = responseObserver.getResponse();
        assertThat(response.getAllUsersList())
                .hasSize(1)
                .first()
                .extracting(User::getUsername)
                .isEqualTo("frienduser");
    }

    @Test
    void deleteFriendshipShouldRemoveRelationship() {
        FriendshipEntity friendship = createFriendship(testUser, friendUser, FriendshipStatus.ACCEPTED);
        when(userRepository.findById(UUID.fromString(testUserId.toString()))).thenReturn(Optional.of(testUser));
        when(userRepository.findById(UUID.fromString(friendUserId.toString()))).thenReturn(Optional.of(friendUser));
        when(friendshipRepository.findFriendship(eq(testUser), eq(friendUser))).thenReturn(Optional.of(friendship));

        TestStreamObserver<User> responseObserver = new TestStreamObserver<>();
        userDataService.updateUserFriendship(
                UpdateUserFriendshipRequest.newBuilder()
                        .setActionAuthorUserId(testUserId.toString())
                        .setActionTargetUserId(friendUserId.toString())
                        .setAction(FriendshipAction.DELETE)
                        .build(),
                responseObserver
        );

        verify(friendshipRepository).delete(friendship);
        assertThat(responseObserver.getResponse().getFriendStatus()).isEqualTo(FriendStatus.NOT_FRIEND);
    }

    private UserEntity createTestUser(UUID id, String username) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setFirstname("Test");
        user.setLastName("User");
        user.setAvatar(new byte[0]);
        user.setCountryId("RU");
        return user;
    }

    private FriendshipEntity createFriendship(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(requester);
        friendship.setAddressee(addressee);
        friendship.setStatus(status);
        friendship.setCreatedDate(Timestamp.from(Instant.now()));
        return friendship;
    }

    private static class TestStreamObserver<T> implements StreamObserver<T> {
        private T response;
        private Throwable error;

        @Override
        public void onNext(T value) {
            this.response = value;
        }

        @Override
        public void onError(Throwable t) {
            this.error = t;
        }

        @Override
        public void onCompleted() {
        }

        public T getResponse() {
            if (error != null) {
                throw new AssertionError("Unexpected error: " + error.getMessage(), error);
            }
            return response;
        }

        public Throwable getError() {
            return error;
        }
    }
}