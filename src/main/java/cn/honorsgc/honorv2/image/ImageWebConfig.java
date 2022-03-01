package cn.honorsgc.honorv2.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageWebConfig implements WebMvcConfigurer {
    @Autowired
    private ImageConfig imageConfig;
    private static final Logger logger = LoggerFactory.getLogger(ImageWebConfig.class);
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("配置文件已经生效");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+imageConfig.getPath());
    }
}
