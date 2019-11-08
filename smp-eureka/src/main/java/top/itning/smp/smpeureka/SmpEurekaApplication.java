package top.itning.smp.smpeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author itning
 */
@SpringBootApplication
@EnableEurekaServer
public class SmpEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpEurekaApplication.class, args);
    }

}
