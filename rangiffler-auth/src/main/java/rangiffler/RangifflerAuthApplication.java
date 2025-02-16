package rangiffler;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rangiffler.service.PropertiesLogger;

@SpringBootApplication
public class RangifflerAuthApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RangifflerAuthApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}
