package rangiffler.guicebinding;

import com.google.inject.AbstractModule;
import rangiffler.db.repository.*;
import rangiffler.service.UserService;
import rangiffler.service.impl.UserDbService;


public class BasicModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(CountryRepository.class).to(CountryRepositoryJdbc.class);
    bind(UserRepository.class).to(UserRepositoryJdbc.class);
    bind(FriendshipRepository.class).to(FriendshipRepositoryJdbc.class);
    bind(PhotoRepository.class).to(PhotoRepositoryJdbc.class);
    bind(UserService.class).to(UserDbService.class);
  }
}
