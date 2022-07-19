package phone.phoneorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;

@SpringBootApplication(exclude = ArtemisAutoConfiguration.class)
public class PhoneOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoneOrderServiceApplication.class, args);
    }

}
