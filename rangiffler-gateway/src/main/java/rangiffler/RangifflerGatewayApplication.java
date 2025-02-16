package rangiffler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rangiffler.service.PropertiesLogger;
@Slf4j
@SpringBootApplication
public class RangifflerGatewayApplication {

  public static void main(String[] args) {
    log.info("Запуск RangifflerGatewayApplication...");
    SpringApplication springApplication = new SpringApplication(RangifflerGatewayApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
    log.info("RangifflerGatewayApplication успешно запущен.");
  }
}
