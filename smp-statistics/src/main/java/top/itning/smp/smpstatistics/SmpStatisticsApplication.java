package top.itning.smp.smpstatistics;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmpStatisticsApplication.class, args);
    }

}
