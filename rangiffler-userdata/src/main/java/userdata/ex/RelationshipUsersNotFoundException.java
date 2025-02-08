package userdata.ex;

import userdata.data.PartnerStatus;

public class RelationshipUsersNotFoundException extends RuntimeException {

  private final String currentUsername, partnerUsername;
  private final PartnerStatus status;

  public RelationshipUsersNotFoundException(String currentUsername,
                                            String partnerUsername,
                                            PartnerStatus status) {
    this.currentUsername = currentUsername;
    this.partnerUsername = partnerUsername;
    this.status = status;
  }

  @Override
  public String getMessage() {
    return String.format("У пользователя '%s' не найдена связь '%s' с пользователем '%s'",
            currentUsername, status, partnerUsername);
  }

}