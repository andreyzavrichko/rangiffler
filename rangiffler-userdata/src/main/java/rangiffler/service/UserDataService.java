package rangiffler.service;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import rangiffler.data.FriendshipEntity;
import rangiffler.data.FriendshipStatus;
import rangiffler.data.UserEntity;
import rangiffler.data.repository.FriendshipRepository;
import rangiffler.data.repository.UserRepository;
import rangiffler.exception.FriendshipNotFoundException;
import rangiffler.exception.FriendshipRequestNotFoundException;
import rangiffler.exception.UserNotFoundException;
import rangiffler.grpc.*;
import rangiffler.mapper.UserEntityMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * gRPC сервис для управления данными пользователей и действиями дружбы.
 * Этот сервис предоставляет методы для получения информации о пользователях, управления дружбами,
 * а также обновления данных пользователей.
 */
@GrpcService
public class UserDataService extends RangifflerUserdataServiceGrpc.RangifflerUserdataServiceImplBase {

  private final UserRepository userRepository;
  private final FriendshipRepository friendshipRepository;

  /**
   * Конструктор для UserDataService, инициализирующий репозитории.
   * @param userRepository репозиторий пользователей для управления данными
   * @param friendshipRepository репозиторий дружбы для управления дружбами
   */
  @Autowired
  public UserDataService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
    this.userRepository = userRepository;
    this.friendshipRepository = friendshipRepository;
  }

  /**
   * Получает всех пользователей, кроме запрашивающего, с возможностью фильтрации по запросу поиска.
   * @param request запрос, содержащий имя пользователя, информацию для пагинации и запрос поиска
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getAllUsers(AllUsersRequest request, StreamObserver<AllUsersResponse> responseObserver) {
    var user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var allUsersEntities = userRepository.findByUsernameNotAndSearchQuery(request.getUsername(),
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );
    var friendStatuses = getPeopleFriendStatuses(user, allUsersEntities.stream().toList());

    var allUsersResponse = AllUsersResponse.newBuilder().addAllAllUsers(
            allUsersEntities.stream()
                .map(s -> {
                      var friendStatus = friendStatuses.get(s.getId());
                      return UserEntityMapper.toMessage(s, friendStatus);
                    }
                )
                .toList()
        )
        .setHasNext(allUsersEntities.hasNext())
        .build();

    responseObserver.onNext(allUsersResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает информацию о пользователе по его имени.
   * @param request запрос, содержащий имя пользователя
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getUser(UserRequest request, StreamObserver<User> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var userResponse = UserEntityMapper.toMessage(userEntity);

    responseObserver.onNext(userResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает информацию о пользователе по его ID.
   * @param request запрос, содержащий ID пользователя
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getUserById(UserByIdRequest request, StreamObserver<User> responseObserver) {
    var userEntity = userRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new UserNotFoundException(request.getId()));
    var userResponse = UserEntityMapper.toMessage(userEntity);

    responseObserver.onNext(userResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает список друзей пользователя.
   * @param request запрос, содержащий имя пользователя, информацию для пагинации и запрос поиска
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getUserFriends(AllUsersRequest request, StreamObserver<AllUsersResponse> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var allUsersEntities = userRepository.findFriends(userEntity,
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );

    var allUsersResponse = AllUsersResponse.newBuilder().addAllAllUsers(
            allUsersEntities.stream().map(s -> UserEntityMapper.toMessage(s, FriendStatus.FRIEND)).toList()
        )
        .setHasNext(allUsersEntities.hasNext())
        .build();

    responseObserver.onNext(allUsersResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает список ID друзей пользователя.
   * @param request запрос, содержащий имя пользователя
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getUserFriendsIds(UserRequest request, StreamObserver<UserIdsResponse> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var userFriendIds = userRepository.findFriendsIds(userEntity);

    var idsResponse = UserIdsResponse.newBuilder()
        .addAllUserIds(userFriendIds.stream().map(UUID::toString).toList())
        .build();

    responseObserver.onNext(idsResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает входящие дружеские запросы для пользователя.
   * @param request запрос, содержащий имя пользователя, информацию для пагинации и запрос поиска
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getFriendshipRequests(AllUsersRequest request, StreamObserver<AllUsersResponse> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var allUsersEntities = userRepository.findIncomeInvitations(userEntity,
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );

    var allUsersResponse = AllUsersResponse.newBuilder().addAllAllUsers(
            allUsersEntities.stream().map(s -> UserEntityMapper.toMessage(s, FriendStatus.INVITATION_RECEIVED)).toList()
        )
        .setHasNext(allUsersEntities.hasNext())
        .build();

    responseObserver.onNext(allUsersResponse);
    responseObserver.onCompleted();
  }

  /**
   * Получает исходящие дружеские запросы для пользователя.
   * @param request запрос, содержащий имя пользователя, информацию для пагинации и запрос поиска
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Override
  public void getFriendshipAddresses(AllUsersRequest request, StreamObserver<AllUsersResponse> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    var allUsersEntities = userRepository.findOutcomeInvitations(userEntity,
        PageRequest.of(request.getPage(), request.getSize()),
        request.getSearchQuery()
    );

    var allUsersResponse = AllUsersResponse.newBuilder().addAllAllUsers(
            allUsersEntities.stream().map(s -> UserEntityMapper.toMessage(s, FriendStatus.INVITATION_SENT)).toList()
        )
        .setHasNext(allUsersEntities.hasNext())
        .build();

    responseObserver.onNext(allUsersResponse);
    responseObserver.onCompleted();
  }

  /**
   * Обновляет данные пользователя.
   * @param request запрос с обновленными данными пользователя
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Transactional
  @Override
  public void updateUser(User request, StreamObserver<User> responseObserver) {
    var userEntity = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
    userEntity.setFirstname(request.getFirstname());
    userEntity.setLastName(request.getLastName());
    userEntity.setAvatar(request.getAvatar().toByteArray());
    userEntity.setCountryId(request.getCountryId());
    userRepository.save(userEntity);

    var userResponse = UserEntityMapper.toMessage(userEntity);

    responseObserver.onNext(userResponse);
    responseObserver.onCompleted();
  }

  /**
   * Обновляет состояние дружбы между двумя пользователями.
   * @param request запрос с данными для обновления состояния дружбы
   * @param responseObserver наблюдатель для отправки ответа
   */
  @Transactional
  @Override
  public void updateUserFriendship(UpdateUserFriendshipRequest request, StreamObserver<User> responseObserver) {
    var actionAuthorUser = userRepository.findById(UUID.fromString(request.getActionAuthorUserId()))
        .orElseThrow(() -> new UserNotFoundException(request.getActionAuthorUserId()));
    var actionTargetUser = userRepository.findById(UUID.fromString(request.getActionTargetUserId()))
        .orElseThrow(() -> new UserNotFoundException(request.getActionTargetUserId()));

    var action = request.getAction();
    User userResponse;
    switch (action) {
      case ADD -> {
        var friendshipEntity = new FriendshipEntity();
        friendshipEntity.setRequester(actionAuthorUser);
        friendshipEntity.setAddressee(actionTargetUser);
        friendshipEntity.setStatus(FriendshipStatus.PENDING);
        friendshipEntity.setCreatedDate(Timestamp.from(Instant.now()));
        friendshipRepository.saveAndFlush(friendshipEntity);
        userResponse = UserEntityMapper.toMessage(actionTargetUser, FriendStatus.INVITATION_SENT);
      }
      case DELETE -> {
        var friendshipEntity = friendshipRepository.findFriendship(actionAuthorUser, actionTargetUser).orElseThrow(
            () -> new FriendshipNotFoundException(actionAuthorUser.getUsername(), actionTargetUser.getUsername())
        );
        friendshipRepository.delete(friendshipEntity);
        userResponse = UserEntityMapper.toMessage(actionTargetUser, FriendStatus.NOT_FRIEND);
      }
      case ACCEPT -> {
        var friendshipEntity = friendshipRepository.findByRequesterAndAddresseeAndStatus(actionTargetUser,
                actionAuthorUser, FriendshipStatus.PENDING)
            .orElseThrow(() -> new FriendshipRequestNotFoundException(
                    actionTargetUser.getUsername(), actionAuthorUser.getUsername()
                )
            );
        friendshipEntity.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.saveAndFlush(friendshipEntity);
        userResponse = UserEntityMapper.toMessage(actionTargetUser, FriendStatus.FRIEND);
      }
      case REJECT -> {
        var friendshipEntity = friendshipRepository.findByRequesterAndAddresseeAndStatus(actionTargetUser,
                actionAuthorUser, FriendshipStatus.PENDING)
            .orElseThrow(() -> new FriendshipRequestNotFoundException(
                    actionTargetUser.getUsername(), actionAuthorUser.getUsername()
                )
            );
        friendshipRepository.delete(friendshipEntity);
        userResponse = UserEntityMapper.toMessage(actionTargetUser, FriendStatus.NOT_FRIEND);
      }
      default -> throw new IllegalStateException();
    }

    responseObserver.onNext(userResponse);
    responseObserver.onCompleted();
  }

  /**
   * Метод для получения статуса дружбы между пользователями.
   * @param actor текущий пользователь
   * @param users все пользователи
   * @return карта с ID пользователя и его статусом дружбы
   */
  private Map<UUID, FriendStatus> getPeopleFriendStatuses(UserEntity actor, List<UserEntity> users) {
    var friendships = friendshipRepository.findFriendships(users, actor);
    return users.stream()
        .collect(Collectors.toMap(
                UserEntity::getId,
                userEntity -> {
                  var friendshipEntity = friendships.stream().filter(
                          s -> s.getAddressee().getId().equals(userEntity.getId())
                              || s.getRequester().getId().equals(userEntity.getId())
                      )
                      .findFirst()
                      .orElse(null);
                  if (friendshipEntity == null) {
                    return FriendStatus.NOT_FRIEND;
                  } else if (friendshipEntity.getStatus() == FriendshipStatus.ACCEPTED) {
                    return FriendStatus.FRIEND;
                  } else if (friendshipEntity.getRequester().getId().equals(actor.getId())) {
                    return FriendStatus.INVITATION_SENT;
                  } else {
                    return FriendStatus.INVITATION_RECEIVED;
                  }
                }
            )
        );
  }
}
