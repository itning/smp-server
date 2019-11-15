package top.itning.smp.smpclass;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpClassApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpClassApplication.class, args);
    }

}
