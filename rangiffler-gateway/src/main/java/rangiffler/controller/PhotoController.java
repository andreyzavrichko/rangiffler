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
import rangiffler.client.PhotoClient;
import rangiffler.model.GqlFeed;
import rangiffler.model.GqlPhoto;
import rangiffler.model.PhotoInput;

import java.util.UUID;

/**
 * Контроллер для работы с фотографиями.
 * Этот контроллер предоставляет операции для получения ленты фотографий,
 * добавления новой фотографии и удаления существующих фотографий через GraphQL.
 */
@Controller
public class PhotoController {

  private final PhotoClient photoClient;

  /**
   * Конструктор для инициализации PhotoController с зависимостью от PhotoClient.
   *
   * @param photoClient клиент для работы с фотографиями
   */
  @Autowired
  public PhotoController(PhotoClient photoClient) {
    this.photoClient = photoClient;
  }

  /**
   * Получение ленты фотографий.
   * Этот метод возвращает ленту фотографий, с возможностью фильтрации по друзьям.
   *
   * @param principal объект JWT, представляющий аутентифицированного пользователя
   * @param withFriends флаг, определяющий, включать ли фотографии с друзьями
   * @return объект GqlFeed с лентой фотографий
   */
  @QueryMapping
  public GqlFeed feed(@AuthenticationPrincipal Jwt principal,
                      @Argument Boolean withFriends) {
    return new GqlFeed(principal.getClaim("sub"), withFriends, null, null);
  }

  /**
   * Получение фотографий для ленты.
   * Этот метод выполняет запрос к PhotoClient для получения фотографий на указанной странице
   * с учетом пагинации.
   *
   * @param feed объект GqlFeed, который содержит информацию о ленте
   * @param principal объект JWT, представляющий аутентифицированного пользователя
   * @param page номер страницы для пагинации
   * @param size размер страницы (количество фотографий на странице)
   * @return объект Slice, содержащий фотографии (GqlPhoto)
   */
  @SchemaMapping(typeName = "Feed", field = "photos")
  public Slice<GqlPhoto> photos(GqlFeed feed, @AuthenticationPrincipal Jwt principal,
                                @Argument int page,
                                @Argument int size) {
    return photoClient.getPhotos(principal.getClaim("sub"), page, size, feed.withFriends());
  }

  /**
   * Добавление новой фотографии.
   * Этот метод позволяет пользователю загрузить фотографию, предоставив необходимые данные.
   *
   * @param principal объект JWT, представляющий аутентифицированного пользователя
   * @param input объект PhotoInput, содержащий данные для загрузки фотографии
   * @return объект GqlPhoto, представляющий загруженную фотографию
   */
  @MutationMapping
  public GqlPhoto photo(@AuthenticationPrincipal Jwt principal, @Argument PhotoInput input) {
    return photoClient.photo(principal.getClaim("sub"), input);
  }

  /**
   * Удаление фотографии.
   * Этот метод позволяет пользователю удалить фотографию по указанному идентификатору.
   *
   * @param principal объект JWT, представляющий аутентифицированного пользователя
   * @param id UUID идентификатор фотографии, которую нужно удалить
   * @return true, если удаление прошло успешно, иначе false
   */
  @MutationMapping
  public boolean deletePhoto(@AuthenticationPrincipal Jwt principal, @Argument UUID id) {
    return photoClient.deletePhoto(principal.getClaim("sub"), id);
  }
}
