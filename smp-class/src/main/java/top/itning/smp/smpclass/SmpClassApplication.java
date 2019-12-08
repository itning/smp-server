package top.itning.smp.smpclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author itning
 */
@SpringCloudApplication
@EnableFeignClients
public class SmpClassApplication {
    static {
        Logger logger = LoggerFactory.getLogger(SmpClassApplication.class);
        try {
            com.lzw.face.SeetafaceBuilder.build();
            logger.info("Face Recognition Init Success");
        } catch (Throwable e) {
            logger.error("Face Recognition Init Fail", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SmpClassApplication.class, args);
    }

}
