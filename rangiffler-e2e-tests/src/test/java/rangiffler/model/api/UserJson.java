package rangiffler.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import rangiffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("username") String username,
        @JsonProperty("firstname") String firstname,
        @JsonProperty("lastname") String lastname,
        @JsonProperty("avatar") String avatar,
        @JsonProperty("countryId") String countryId,
        @JsonIgnore TestData testData
) {

    public UserJson(@Nonnull String username) {
        this(username, null);
    }

    public UserJson(@Nonnull String username, @Nullable TestData testData) {
        this(null, username, null, null, null,"4cc91f80-f195-11ee-9b32-0242ac110002", testData);
    }

    public UserJson addTestData(@Nonnull TestData testData) {
        return new UserJson(id, username, firstname, lastname, avatar, countryId, testData);
    }

    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getAvatar() != null && entity.getAvatar().length > 0
                        ? new String(entity.getAvatar(), StandardCharsets.UTF_8)
                        : null,
                "4cc91f80-f195-11ee-9b32-0242ac110002",
                null
        );
    }
}
