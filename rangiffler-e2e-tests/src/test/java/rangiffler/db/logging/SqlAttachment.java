package rangiffler.db.logging;

import io.qameta.allure.attachment.AttachmentData;
import lombok.Getter;

@Getter
public class SqlAttachment implements AttachmentData {

  private final String name;
  private final String sql;

  public SqlAttachment(String name, String sql) {
    this.name = name;
    this.sql = sql;
  }

}
