package geo.data.entity;

import jakarta.persistence.*;
import lombok.*;
import rangiffler.grpc.Country;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "country")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Country toGrpc() {
        if (id == null) {
            throw new IllegalStateException("CountryEntity id is null. Ensure the entity is persisted before converting to gRPC message.");
        }
        return Country.newBuilder()
                .setId(id.toString())
                .setCode(code)
                .setName(name)
                .build();
    }

}