package top.itning.smp.smproom;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpRoomApplication.class, args);
    }

}
