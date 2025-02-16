package rangiffler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import rangiffler.client.GeoClient;
import rangiffler.model.GqlCountry;
import rangiffler.model.GqlPhoto;
import rangiffler.model.GqlStat;
import rangiffler.model.GqlUser;

import java.util.List;

/**
 * Контроллер для работы с географической информацией.
 * Этот контроллер отвечает за получение данных о странах и их привязку
 * к пользователю, фотографии и статистике через GraphQL.
 */
@Controller
public class GeoController {

  private final GeoClient geoClient;

  /**
   * Конструктор для инициализации GeoController с зависимостью от GeoClient.
   *
   * @param geoClient клиент для работы с данными о странах
   */
  @Autowired
  public GeoController(GeoClient geoClient) {
    this.geoClient = geoClient;
  }

  /**
   * Получение списка всех стран.
   *
   * Этот метод выполняет запрос к GeoClient для получения списка всех стран.
   *
   * @return список стран в формате GqlCountry
   */
  @QueryMapping
  public List<GqlCountry> countries() {
    return geoClient.getAllCountries();
  }

  /**
   * Получение информации о стране пользователя.
   * Этот метод привязывает поле "location" пользователя к данным о стране,
   * используя информацию из клиента GeoClient.
   *
   * @param user объект пользователя, для которого необходимо получить страну
   * @return объект GqlCountry, представляющий страну пользователя
   */
  @SchemaMapping(typeName = "User", field = "location")
  public GqlCountry userCountry(GqlUser user) {
    return geoClient.getCountry(user.location().id());
  }

  /**
   * Получение информации о стране для фотографии.
   * Этот метод привязывает поле "country" фотографии к данным о стране,
   * используя информацию из клиента GeoClient.
   *
   * @param photo объект фотографии, для которой необходимо получить страну
   * @return объект GqlCountry, представляющий страну фотографии
   */
  @SchemaMapping(typeName = "Photo", field = "country")
  public GqlCountry country(GqlPhoto photo) {
    return geoClient.getCountry(photo.country().id());
  }

  /**
   * Получение информации о стране для статистики.
   * Этот метод привязывает поле "country" статистики к данным о стране,
   * используя информацию из клиента GeoClient.
   *
   * @param stat объект статистики, для которой необходимо получить страну
   * @return объект GqlCountry, представляющий страну статистики
   */
  @SchemaMapping(typeName = "Stat", field = "country")
  public GqlCountry country(GqlStat stat) {
    return geoClient.getCountry(stat.country().id());
  }
}
