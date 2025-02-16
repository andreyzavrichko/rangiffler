package rangiffler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rangiffler.data.UserEntity;
import rangiffler.data.repository.UserRepository;
import rangiffler.domain.RangifflerUserPrincipal;

/**
 * Сервис для загрузки данных пользователя по имени пользователя.
 * Реализует интерфейс {@link UserDetailsService} для интеграции с Spring Security.
 */
@Component
public class RangifflerUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Конструктор для внедрения зависимости {@link UserRepository}.
   *
   * @param userRepository репозиторий для доступа к данным пользователей.
   */
  @Autowired
  public RangifflerUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Загружает пользователя по имени пользователя. Если пользователь не найден, выбрасывается
   * исключение {@link UsernameNotFoundException}.
   *
   * @param username имя пользователя, который должен быть найден.
   * @return объект {@link UserDetails} для найденного пользователя.
   * @throws UsernameNotFoundException если пользователь с таким именем не найден.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Попытка найти пользователя по имени
    UserEntity user = userRepository.findByUsername(username);

    // Если пользователь не найден, выбрасываем исключение с пояснением
    if (user == null) {
      throw new UsernameNotFoundException("Username: " + username + " not found");
    }

    // Создаем объект RangifflerUserPrincipal на основе найденного пользователя
    return new RangifflerUserPrincipal(user);
  }
}
