package rangiffler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rangiffler.data.Authority;
import rangiffler.data.AuthorityEntity;
import rangiffler.data.UserEntity;
import rangiffler.data.repository.UserRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RangifflerUserDetailsServiceTest {

  private RangifflerUserDetailsService userDetailsService;
  private UserEntity sampleUserEntity;
  private List<AuthorityEntity> authorityList;

  @BeforeEach
  void setup(@Mock UserRepository userRepository) {
    AuthorityEntity readPermission = new AuthorityEntity();
    readPermission.setUser(sampleUserEntity);
    readPermission.setAuthority(Authority.read);

    AuthorityEntity writePermission = new AuthorityEntity();
    writePermission.setUser(sampleUserEntity);
    writePermission.setAuthority(Authority.write);

    authorityList = List.of(readPermission, writePermission);

    sampleUserEntity = new UserEntity();
    sampleUserEntity.setUsername("validUser");
    sampleUserEntity.setAuthorities(authorityList);
    sampleUserEntity.setEnabled(true);
    sampleUserEntity.setPassword("password123");
    sampleUserEntity.setAccountNonExpired(true);
    sampleUserEntity.setAccountNonLocked(true);
    sampleUserEntity.setCredentialsNonExpired(true);
    sampleUserEntity.setId(UUID.randomUUID());

    lenient().when(userRepository.findByUsername("validUser"))
            .thenReturn(sampleUserEntity);

    lenient().when(userRepository.findByUsername(not(eq("validUser"))))
            .thenReturn(null);

    userDetailsService = new RangifflerUserDetailsService(userRepository);
  }

  @Test
  void shouldLoadUserDetailsForValidUsername() {
    UserDetails userDetails = userDetailsService.loadUserByUsername("validUser");

    List<SimpleGrantedAuthority> expectedAuthorities = authorityList.stream()
            .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
            .toList();

    assertEquals("validUser", userDetails.getUsername());
    assertEquals("password123", userDetails.getPassword());
    assertEquals(expectedAuthorities, userDetails.getAuthorities());

    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());
    assertTrue(userDetails.isEnabled());
  }

  @Test
  void shouldThrowUsernameNotFoundExceptionForInvalidUsername() {
    UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("invalidUser")
    );

    assertEquals("Username: invalidUser not found", exception.getMessage());
  }
}
