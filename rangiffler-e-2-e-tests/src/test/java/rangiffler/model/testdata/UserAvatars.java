package rangiffler.model.testdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserAvatars {

  DEFAULT("defaultAvatar.png"),
  DOG("dog.png");

  private final String fileName;
}
