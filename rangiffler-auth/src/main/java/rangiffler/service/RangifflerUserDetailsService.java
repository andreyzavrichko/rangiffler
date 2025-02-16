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

@Component
public class RangifflerUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public RangifflerUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Username: " + username + " not found");
    }
    return new RangifflerUserPrincipal(user);
  }
}
