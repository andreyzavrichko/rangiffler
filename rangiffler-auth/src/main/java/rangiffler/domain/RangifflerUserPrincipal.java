package rangiffler.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rangiffler.data.UserEntity;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Реализация пользовательских данных для использования в Spring Security.
 */
public class RangifflerUserPrincipal implements UserDetails {

  private final UserEntity user;

  /**
   * Конструктор для создания экземпляра RangifflerUserPrincipal.
   *
   * @param user объект сущности пользователя.
   */
  public RangifflerUserPrincipal(UserEntity user) {
    this.user = user;
  }

  /**
   * Возвращает список ролей пользователя.
   *
   * @return коллекция ролей пользователя.
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities().stream()
            .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
            .collect(Collectors.toList());
  }

  /**
   * Возвращает пароль пользователя.
   *
   * @return пароль пользователя.
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * Возвращает имя пользователя.
   *
   * @return имя пользователя.
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Проверяет, не истек ли срок действия учетной записи.
   *
   * @return true, если учетная запись не истекла, иначе false.
   */
  @Override
  public boolean isAccountNonExpired() {
    return user.getAccountNonExpired();
  }

  /**
   * Проверяет, не заблокирована ли учетная запись.
   *
   * @return true, если учетная запись не заблокирована, иначе false.
   */
  @Override
  public boolean isAccountNonLocked() {
    return user.getAccountNonLocked();
  }

  /**
   * Проверяет, не истекли ли учетные данные пользователя.
   *
   * @return true, если учетные данные не истекли, иначе false.
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return user.getCredentialsNonExpired();
  }

  /**
   * Проверяет, включена ли учетная запись.
   *
   * @return true, если учетная запись включена, иначе false.
   */
  @Override
  public boolean isEnabled() {
    return user.getEnabled();
  }
}
