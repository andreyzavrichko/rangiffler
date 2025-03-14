package rangiffler.model.testdata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhotoFiles {

  CALIFORNIA("California.jpg"),
  CHINA("China.jpg");

  private final String fileName;
}
