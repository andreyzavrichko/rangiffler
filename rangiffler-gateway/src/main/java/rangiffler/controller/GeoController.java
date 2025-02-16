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

@Controller
public class GeoController {

  private final GeoClient geoClient;

  @Autowired
  public GeoController(GeoClient geoClient) {
    this.geoClient = geoClient;
  }

  @QueryMapping
  public List<GqlCountry> countries() {
    return geoClient.getAllCountries();
  }

  @SchemaMapping(typeName = "User", field = "location")
  public GqlCountry userCountry(GqlUser user) {
    return geoClient.getCountry(user.location().id());
  }

  @SchemaMapping(typeName = "Photo", field = "country")
  public GqlCountry country(GqlPhoto photo) {
    return geoClient.getCountry(photo.country().id());
  }

  @SchemaMapping(typeName = "Stat", field = "country")
  public GqlCountry country(GqlStat stat) {
    return geoClient.getCountry(stat.country().id());
  }
}
