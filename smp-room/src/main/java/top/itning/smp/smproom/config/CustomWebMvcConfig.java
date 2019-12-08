package top.itning.smp.smproom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC参数配置
 *
 * @author itning
 */
@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {
    private final CustomProperties customProperties;

    @Autowired
    public CustomWebMvcConfig(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/check_image/**").addResourceLocations("file:" + customProperties.getResourceLocation());
        registry.addResourceHandler("/face_image/**").addResourceLocations("file:" + customProperties.getFaceLocation());
    }
}
