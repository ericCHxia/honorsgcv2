package cn.honorsgc.honorv2.hduhelper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.hdu.honorv2.hduhelper")
@Data
public class HduHelperConfig {
    String id;
    String secret;
    String baseUrl="https://api.hduhelp.com";
}
