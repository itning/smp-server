package top.itning.smp.smpsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpSecurityApplication.class, args);
    }

}
