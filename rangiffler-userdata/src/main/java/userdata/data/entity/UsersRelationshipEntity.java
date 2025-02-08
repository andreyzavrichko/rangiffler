package userdata.data.entity;

import jakarta.persistence.*;
import lombok.*;
import rangiffler.grpc.RelationshipResponse;
import userdata.data.PartnerStatus;
import userdata.data.UsersRelationshipId;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UsersRelationshipId.class)
@Table(name = "users_relationship")
public class UsersRelationshipEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id", nullable = false)
    private UserEntity partner;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PartnerStatus status;

    public RelationshipResponse toGrpc() {
        return RelationshipResponse.newBuilder()
                .setUser(user.toGrpc())
                .setPartner(partner.toGrpc())
                .setStatus(status.toString())
                .build();
    }

}