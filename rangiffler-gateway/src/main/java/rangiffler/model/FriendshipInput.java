package rangiffler.model;

import java.util.UUID;

public record FriendshipInput(UUID user, FriendshipAction action) {

}
