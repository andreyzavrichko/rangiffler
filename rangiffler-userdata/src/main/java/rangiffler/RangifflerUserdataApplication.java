package rangiffler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Slf4j
@SpringBootApplication
public class RangifflerUserdataApplication {

  public static void main(String[] args) {
    log.info("Запуск RangifflerUserdataApplication...");
    SpringApplication.run(RangifflerUserdataApplication.class, args);
    log.info("RangifflerUserdataApplication успешно запущен.");
  }
}
