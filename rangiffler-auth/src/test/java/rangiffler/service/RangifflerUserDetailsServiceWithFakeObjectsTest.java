package rangiffler.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class RangifflerUserDetailsServiceWithFakeObjectsTest {

  private RangifflerUserDetailsService userDetailsService;
  private UserEntity mockUserEntity;
  private List<AuthorityEntity> authorityList;

  @BeforeEach
  void setupFakeRepository() {
    var fakeRepository = new UserRepository.Fake();

    AuthorityEntity readPermission = new AuthorityEntity();
    readPermission.setUser(mockUserEntity);
    readPermission.setAuthority(Authority.read);

    AuthorityEntity writePermission = new AuthorityEntity();
    writePermission.setUser(mockUserEntity);
    writePermission.setAuthority(Authority.write);

    authorityList = List.of(readPermission, writePermission);

    mockUserEntity = new UserEntity();
    mockUserEntity.setUsername("validUser");
    mockUserEntity.setAuthorities(authorityList);
    mockUserEntity.setEnabled(true);
    mockUserEntity.setPassword("password123");
    mockUserEntity.setAccountNonExpired(true);
    mockUserEntity.setAccountNonLocked(true);
    mockUserEntity.setCredentialsNonExpired(true);
    mockUserEntity.setId(UUID.randomUUID());

    fakeRepository.withUserEntities(List.of(mockUserEntity));

    userDetailsService = new RangifflerUserDetailsService(fakeRepository);
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
