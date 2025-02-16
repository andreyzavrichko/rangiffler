package rangiffler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rangiffler.service.PropertiesLogger;
@Slf4j
@SpringBootApplication
public class RangifflerAuthApplication {

  public static void main(String[] args) {
    log.info("Запуск RangifflerAuthApplication...");
    SpringApplication springApplication = new SpringApplication(RangifflerAuthApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
    log.info("RangifflerCountryApplication успешно запущен.");
  }
}
