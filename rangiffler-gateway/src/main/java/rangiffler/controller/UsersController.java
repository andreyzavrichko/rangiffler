package rangiffler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.client.UsersClient;
import rangiffler.model.FriendshipInput;
import rangiffler.model.GqlLike;
import rangiffler.model.GqlUser;
import rangiffler.model.UserInput;

import javax.annotation.Nullable;

/**
 * Контроллер для работы с пользователями.
 * Этот контроллер управляет запросами и мутациями, связанными с пользователями,
 * их друзьями, приглашениями в друзья, и лайками через GraphQL.
 */
@Controller
public class UsersController {

  private final UsersClient usersClient;

  /**
   * Конструктор для инициализации UsersController с зависимостью от UsersClient.
   *
   * @param usersClient клиент для работы с пользователями
   */
  @Autowired
  public UsersController(UsersClient usersClient) {
    this.usersClient = usersClient;
  }

  /**
   * Получение списка пользователей с пагинацией и возможностью поиска.
   *
   * @param principal аутентифицированный пользователь (JWT токен)
   * @param page номер страницы
   * @param size количество пользователей на странице
   * @param searchQuery строка для поиска пользователей
   * @return Slice с результатами поиска пользователей
   */
  @QueryMapping
  public Slice<GqlUser> users(@AuthenticationPrincipal Jwt principal,
                              @Argument int page,
                              @Argument int size,
                              @Argument @Nullable String searchQuery) {
    return usersClient.getAllUsers(principal.getClaim("sub"), page, size, searchQuery);
  }

  /**
   * Получение данных текущего пользователя.
   *
   * @param principal аутентифицированный пользователь (JWT токен)
   * @return GqlUser объект текущего пользователя
   */
  @QueryMapping
  public GqlUser user(@AuthenticationPrincipal Jwt principal) {
    return usersClient.getUser(principal.getClaim("sub"));
  }

  /**
   * Получение списка друзей пользователя с пагинацией и возможностью поиска.
   *
   * @param user объект пользователя
   * @param page номер страницы
   * @param size количество друзей на странице
   * @param searchQuery строка для поиска друзей
   * @return Slice с друзьями пользователя
   */
  @SchemaMapping(typeName = "User", field = "friends")
  public Slice<GqlUser> friends(GqlUser user,
                                @Argument int page,
                                @Argument int size,
                                @Argument @Nullable String searchQuery) {
    return usersClient.getFriends(user.username(), page, size, searchQuery);
  }

  /**
   * Получение списка входящих приглашений в друзья с пагинацией и возможностью поиска.
   *
   * @param user объект пользователя
   * @param page номер страницы
   * @param size количество приглашений на странице
   * @param searchQuery строка для поиска
   * @return Slice с входящими приглашениями
   */
  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public Slice<GqlUser> incomeInvitations(GqlUser user,
                                          @Argument int page,
                                          @Argument int size,
                                          @Argument @Nullable String searchQuery) {
    return usersClient.getFriendshipRequests(user.username(), page, size, searchQuery);
  }

  /**
   * Получение списка исходящих приглашений в друзья с пагинацией и возможностью поиска.
   *
   * @param user объект пользователя
   * @param page номер страницы
   * @param size количество приглашений на странице
   * @param searchQuery строка для поиска
   * @return Slice с исходящими приглашениями
   */
  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public Slice<GqlUser> outcomeInvitations(GqlUser user,
                                           @Argument int page,
                                           @Argument int size,
                                           @Argument @Nullable String searchQuery) {
    return usersClient.getFriendshipAddresses(user.username(), page, size, searchQuery);
  }

  /**
   * Получение имени пользователя по ID в лайке.
   *
   * @param like объект GqlLike, содержащий ID пользователя
   * @return имя пользователя, которое было лайкнуто
   */
  @SchemaMapping(typeName = "Like", field = "username")
  public String likeUser(GqlLike like) {
    return usersClient.getUserById(like.user()).username();
  }

  /**
   * Обновление данных пользователя.
   *
   * @param principal аутентифицированный пользователь (JWT токен)
   * @param input новые данные для пользователя
   * @return обновленный объект GqlUser
   */
  @MutationMapping
  public GqlUser user(@AuthenticationPrincipal Jwt principal, @Argument UserInput input) {
    return usersClient.updateUser(principal.getClaim("sub"), input);
  }

  /**
   * Обновление статуса дружбы (подтверждение/отклонение).
   *
   * @param principal аутентифицированный пользователь (JWT токен)
   * @param input входные данные для обновления статуса дружбы
   * @return обновленный объект GqlUser
   */
  @MutationMapping
  public GqlUser friendship(@AuthenticationPrincipal Jwt principal, @Argument FriendshipInput input) {
    return usersClient.updateFriendshipStatus(principal.getClaim("sub"), input);
  }
}
