package top.itning.smp.smpconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author itning
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
public class SmpConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpConfigApplication.class, args);
    }

}
