package top.itning.smp.smproom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author itning
 */
@ConfigurationProperties(prefix = "app")
@Data
@Component
public class CustomProperties {
    /**
     * 资源映射路径
     */
    private String resourceLocation;
}
