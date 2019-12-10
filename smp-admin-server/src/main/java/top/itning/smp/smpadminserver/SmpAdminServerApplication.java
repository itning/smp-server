package top.itning.smp.smpadminserver;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author itning
 */
@SpringBootApplication
@EnableAdminServer
@EnableEurekaClient
public class SmpAdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpAdminServerApplication.class, args);
    }

}
