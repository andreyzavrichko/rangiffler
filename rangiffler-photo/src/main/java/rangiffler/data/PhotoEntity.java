package rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"photo\"")
public class PhotoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
  private UUID id;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "country_id")
  private UUID countryId;

  @Column
  private String description;

  @Column(name = "photo", columnDefinition = "bytea")
  private byte[] photo;

  @Column(name = "created_date")
  private Timestamp createdDate;

  @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  @JoinTable(
      name = "photo_like",
      joinColumns = {@JoinColumn(name = "photo_id")},
      inverseJoinColumns = {@JoinColumn(name = "like_id")}
  )
  private Set<LikeEntity> likes = new HashSet<>();
}
