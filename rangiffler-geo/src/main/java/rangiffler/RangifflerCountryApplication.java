package rangiffler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class RangifflerCountryApplication {

  public static void main(String[] args) {
    log.info("Запуск RangifflerCountryApplication...");
    SpringApplication.run(RangifflerCountryApplication.class, args);
    log.info("RangifflerCountryApplication успешно запущен.");
  }
}
