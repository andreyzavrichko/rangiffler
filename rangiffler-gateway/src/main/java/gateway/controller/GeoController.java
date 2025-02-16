package gateway.controller;


import gateway.client.GeoClient;
import gateway.model.GqlCountry;
import gateway.model.GqlPhoto;
import gateway.model.GqlStat;
import gateway.model.GqlUser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

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
