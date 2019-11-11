package top.itning.smp.smpleave;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpLeaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpLeaveApplication.class, args);
    }

}
