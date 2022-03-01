package cn.honorsgc.honorv2.image;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Configuration
@ConfigurationProperties(prefix = "com.hdu.honorv2.image")
@Data
public class ImageConfig {
    private String path;
    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxSize = DataSize.ofMegabytes(5);
    private Integer maxCoverWidth = 500;
    private Integer maxCoverHeight = 500;
    private Float saveQuality = 0.8f;
    private String[] acceptContentTypes = {"image/jpeg","image/png","image/webp","image/gif"};
}
