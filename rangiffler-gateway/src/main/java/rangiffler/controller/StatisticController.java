package rangiffler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import rangiffler.client.StatisticClient;
import rangiffler.model.GqlFeed;
import rangiffler.model.GqlStat;

import java.util.List;

/**
 * Контроллер для работы со статистикой.
 * Этот контроллер предоставляет операции для получения статистики для ленты через GraphQL.
 */
@Controller
public class StatisticController {

  private final StatisticClient statisticClient;

  /**
   * Конструктор для инициализации StatisticController с зависимостью от StatisticClient.
   *
   * @param statisticClient клиент для работы со статистикой
   */
  @Autowired
  public StatisticController(StatisticClient statisticClient) {
    this.statisticClient = statisticClient;
  }

  /**
   * Получение статистики для ленты.
   * Этот метод возвращает статистику для указанной ленты. Для аутентифицированного пользователя
   * возвращается статистика на основе его данных и фильтрации по друзьям.
   *
   * @param feed объект GqlFeed, который содержит информацию о ленте
   * @param principal объект JWT, представляющий аутентифицированного пользователя
   * @return список статистики (GqlStat) для указанной ленты
   */
  @SchemaMapping(typeName = "Feed", field = "stat")
  public List<GqlStat> stat(GqlFeed feed, @AuthenticationPrincipal Jwt principal) {
    // Получение статистики через StatisticClient, используя информацию о пользователе и фильтрацию по друзьям
    return statisticClient.getStatistic(principal.getClaim("sub"), feed.withFriends());
  }
}
