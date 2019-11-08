package top.itning.smp.smpinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpInfoApplication.class, args);
    }

}
