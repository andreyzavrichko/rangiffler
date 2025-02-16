package rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column
  private String firstname;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "avatar", columnDefinition = "bytea")
  private byte[] avatar;

  @Column(name = "country_id")
  private String countryId;
}
