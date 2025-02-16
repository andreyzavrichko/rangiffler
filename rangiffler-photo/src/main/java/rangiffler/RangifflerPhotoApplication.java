package rangiffler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class RangifflerPhotoApplication {

  public static void main(String[] args) {
    log.info("Запуск RangifflerPhotoApplication...");
    SpringApplication.run(RangifflerPhotoApplication.class, args);
    log.info("RangifflerPhotoApplication успешно запущен.");
  }
}
