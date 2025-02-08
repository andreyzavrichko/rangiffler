package userdata.ex;

import userdata.data.PartnerStatus;
import userdata.data.entity.UsersRelationshipEntity;

public class RelationshipUsersFoundException extends RuntimeException {

    private final String currentUsername, partnerUsername;
    private final PartnerStatus status;

    public RelationshipUsersFoundException(String currentUsername,
                                           String partnerUsername,
                                           PartnerStatus status) {
        this.currentUsername = currentUsername;
        this.partnerUsername = partnerUsername;
        this.status = status;
    }

    public RelationshipUsersFoundException(UsersRelationshipEntity entity) {
        this(entity.getUser().getUsername(), entity.getPartner().getUsername(), entity.getStatus());
    }

    @Override
    public String getMessage() {
        return String.format("У пользователя '%s' есть связь '%s' с пользователем '%s'",
                currentUsername, status, partnerUsername);
    }

}