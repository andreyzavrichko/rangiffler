package rangiffler.client;

import com.google.protobuf.ByteString;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import rangiffler.grpc.*;
import rangiffler.mapper.UserMapper;
import rangiffler.model.FriendshipInput;
import rangiffler.model.GqlUser;
import rangiffler.model.UserInput;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Класс для взаимодействия с gRPC сервисом пользователей.
 * Предоставляет методы для получения, обновления и управления данными пользователей,
 * а также для обработки запросов о дружбе.
 */
@Component
public class UsersClient {

  @GrpcClient("grpcUserdataClient")
  private RangifflerUserdataServiceGrpc.RangifflerUserdataServiceBlockingStub rangifflerUserdataServiceBlockingStub;
  @Autowired
  private GeoClient geoClient;

  /**
   * Получает список всех пользователей с поддержкой пагинации и поиска.
   *
   * @param username Имя пользователя для получения данных.
   * @param page Номер страницы.
   * @param size Количество элементов на странице.
   * @param searchQuery Строка для поиска пользователей.
   * @return Срез пользователей, соответствующих запросу.
   */
  public Slice<GqlUser> getAllUsers(String username, int page, int size, String searchQuery) {
    var requestParameters = AllUsersRequest.newBuilder()
            .setUsername(username)
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery)
            .build();
    var response = rangifflerUserdataServiceBlockingStub.getAllUsers(requestParameters);

    var users = response.getAllUsersList()
            .stream()
            .map(UserMapper::fromGrpcMessage)
            .toList();
    return !users.isEmpty()
            ? new SliceImpl<>(users, PageRequest.of(page, size), response.getHasNext())
            : null;
  }

  /**
   * Получает пользователя по имени.
   *
   * @param userName Имя пользователя.
   * @return Данные пользователя.
   */
  public GqlUser getUser(String userName) {
    var request = UserRequest.newBuilder().setUsername(userName).build();
    return UserMapper.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.getUser(request));
  }

  /**
   * Получает пользователя по ID.
   *
   * @param id Идентификатор пользователя.
   * @return Данные пользователя.
   */
  public GqlUser getUserById(UUID id) {
    var request = UserByIdRequest.newBuilder().setId(id.toString()).build();
    return UserMapper.fromGrpcMessage(rangifflerUserdataServiceBlockingStub.getUserById(request));
  }

  /**
   * Получает список друзей пользователя с поддержкой пагинации и поиска.
   *
   * @param username Имя пользователя.
   * @param page Номер страницы.
   * @param size Количество элементов на странице.
   * @param searchQuery Строка для поиска друзей.
   * @return Срез друзей пользователя.
   */
  public Slice<GqlUser> getFriends(String username, int page, int size, String searchQuery) {
    var requestParameters = AllUsersRequest.newBuilder()
            .setUsername(username)
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery)
            .build();
    var response = rangifflerUserdataServiceBlockingStub.getUserFriends(requestParameters);

    var users = response.getAllUsersList()
            .stream()
            .map(UserMapper::fromGrpcMessage)
            .toList();
    return !users.isEmpty()
            ? new SliceImpl<>(users, PageRequest.of(page, size), response.getHasNext())
            : null;
  }

  /**
   * Получает идентификаторы друзей пользователя.
   *
   * @param userName Имя пользователя.
   * @return Список идентификаторов друзей.
   */
  public List<UUID> getUserFriendsIds(String userName) {
    var requestParameters = UserRequest.newBuilder().setUsername(userName).build();
    return rangifflerUserdataServiceBlockingStub.getUserFriendsIds(requestParameters)
            .getUserIdsList().stream()
            .map(UUID::fromString)
            .toList();
  }

  /**
   * Получает список запросов на дружбу с поддержкой пагинации и поиска.
   *
   * @param username Имя пользователя.
   * @param page Номер страницы.
   * @param size Количество элементов на странице.
   * @param searchQuery Строка для поиска.
   * @return Срез запросов на дружбу.
   */
  public Slice<GqlUser> getFriendshipRequests(String username, int page, int size, String searchQuery) {
    var requestParameters = AllUsersRequest.newBuilder()
            .setUsername(username)
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery)
            .build();
    var response = rangifflerUserdataServiceBlockingStub.getFriendshipRequests(requestParameters);

    var users = response.getAllUsersList()
            .stream()
            .map(UserMapper::fromGrpcMessage)
            .toList();
    return !users.isEmpty()
            ? new SliceImpl<>(users, PageRequest.of(page, size), response.getHasNext())
            : null;
  }

  /**
   * Получает список пользователей, к которым был отправлен запрос на дружбу.
   *
   * @param username Имя пользователя.
   * @param page Номер страницы.
   * @param size Количество элементов на странице.
   * @param searchQuery Строка для поиска.
   * @return Срез пользователей, к которым был отправлен запрос на дружбу.
   */
  public Slice<GqlUser> getFriendshipAddresses(String username, int page, int size, String searchQuery) {
    var requestParameters = AllUsersRequest.newBuilder()
            .setUsername(username)
            .setPage(page)
            .setSize(size)
            .setSearchQuery(searchQuery)
            .build();
    var response = rangifflerUserdataServiceBlockingStub.getFriendshipAddresses(requestParameters);

    var users = response.getAllUsersList()
            .stream()
            .map(UserMapper::fromGrpcMessage)
            .toList();
    return !users.isEmpty()
            ? new SliceImpl<>(users, PageRequest.of(page, size), response.getHasNext())
            : null;
  }

  /**
   * Обновляет данные пользователя.
   *
   * @param username Имя пользователя.
   * @param userInput Объект с новыми данными пользователя.
   * @return Обновленные данные пользователя.
   */
  public GqlUser updateUser(String username, UserInput userInput) {
    var country = geoClient.getCountryByCode(userInput.location().code());
    var request = User.newBuilder()
            .setUsername(username)
            .setFirstname(userInput.firstname())
            .setLastName(userInput.surname())
            .setAvatar(ByteString.copyFrom(userInput.avatar().getBytes(StandardCharsets.UTF_8)))
            .setCountryId(country.id().toString())
            .build();

    var response = rangifflerUserdataServiceBlockingStub.updateUser(request);
    return UserMapper.fromGrpcMessage(response);
  }

  /**
   * Обновляет статус дружбы между пользователями.
   *
   * @param username Имя пользователя, который выполняет действие.
   * @param friendshipInput Объект с информацией о действии.
   * @return Обновленные данные пользователя.
   */
  public GqlUser updateFriendshipStatus(String username, FriendshipInput friendshipInput) {
    var actionAuthorUser = getUser(username);
    var request = UpdateUserFriendshipRequest.newBuilder()
            .setActionAuthorUserId(actionAuthorUser.id().toString())
            .setActionTargetUserId(friendshipInput.user().toString())
            .setAction(FriendshipAction.valueOf(friendshipInput.action().name()))
            .build();

    var response = rangifflerUserdataServiceBlockingStub.updateUserFriendship(request);
    return UserMapper.fromGrpcMessage(response);
  }
}
