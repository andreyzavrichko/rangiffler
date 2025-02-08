package userdata.ex;

public class RelationshipWithMyselfException extends RuntimeException {

    private final String username;

    public RelationshipWithMyselfException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return String.format("Связь с пользователем '%s' недостижима", username);
    }

}