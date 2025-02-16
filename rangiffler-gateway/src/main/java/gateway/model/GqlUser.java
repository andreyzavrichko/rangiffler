package gateway.model;

import org.springframework.data.domain.Slice;

import java.util.UUID;

public record GqlUser(
    UUID id,
    String username,
    String firstname,
    String surname,
    String avatar,
    GqlFriendStatus friendStatus,
    Slice<GqlUser> friends,
    Slice<GqlUser> incomeInvitations,
    Slice<GqlUser> outcomeInvitations,
    GqlCountry location
) {

}
