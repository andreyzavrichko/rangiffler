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

@Controller
public class StatisticController {

  private final StatisticClient statisticClient;

  @Autowired
  public StatisticController(StatisticClient statisticClient) {
    this.statisticClient = statisticClient;
  }

  @SchemaMapping(typeName = "Feed", field = "stat")
  public List<GqlStat> stat(GqlFeed feed, @AuthenticationPrincipal Jwt principal) {
    return statisticClient.getStatistic(principal.getClaim("sub"), feed.withFriends());
  }
}
