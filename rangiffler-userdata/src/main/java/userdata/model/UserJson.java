package userdata.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import userdata.data.entity.UserEntity;
import userdata.data.PartnerStatus;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar,
        @JsonProperty("relationships")
        Set<String> relationships // или можете использовать другой тип, если требуется больше информации о связях
) {

    public static UserJson fromEntity(UserEntity entity) {
        Set<String> relationships = entity.getRelationshipUsersByStatus(PartnerStatus.FRIEND).stream()
                .map(user -> user.getUsername())
                .collect(Collectors.toSet());

        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getAvatar() != null && entity.getAvatar().length > 0 ? new String(entity.getAvatar(), StandardCharsets.UTF_8) : null,
                relationships
        );
    }

    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setFirstname(firstname);
        entity.setLastname(lastname);
        if (avatar != null) {
            entity.setAvatar(avatar.getBytes(StandardCharsets.UTF_8));
        }
        // Здесь вы можете установить отношения (relationships) в зависимости от требований
        return entity;
    }
}
