package photo.ex;

import java.util.UUID;

public class PhotoNotFoundException extends RuntimeException {

    private final UUID id;

    public PhotoNotFoundException(UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return String.format("Фото с идентификатором '%s' не найдено", id);
    }

}