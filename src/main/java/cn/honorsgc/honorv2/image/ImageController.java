package cn.honorsgc.honorv2.image;

import cn.honorsgc.honorv2.image.exception.ImageException;
import cn.honorsgc.honorv2.image.exception.ImageNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Pattern;

@Api(tags = "图片管理")
@RestController
@EnableAsync
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    @Autowired
    private ImageService service;
    @Autowired
    private ImageRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Pattern numPattern = Pattern.compile("^[-\\ ]?[\\d]*$");

    @PostMapping("/upload_image")
    public ImageResponse uploadImage(@RequestParam(name = "image") MultipartFile file) throws ImageException {
        Image image = service.save(file);
        service.convert(image);
        service.convertWebp(image);
        return ImageResponse.valuesOf(image);
    }

    @GetMapping("/upload_image/{name}")
    public ImageResponse getImage(@PathVariable String name)throws ImageException{
        if (name.contains(".")){
            name = name.substring(0,name.lastIndexOf("."));
        }
        Optional<Image> optionalImage = repository.findById(name);

        if (optionalImage.isEmpty()){
            throw new ImageNotFoundException();
        }

        return ImageResponse.valuesOf(optionalImage.get());
    }

    @GetMapping("/image/{width}/{name}")
    public ResponseEntity<Resource> getImage(HttpServletRequest request,
                                             @PathVariable String name,
                                             @PathVariable String width) throws ImageException, IOException {
            String acceptHeader = request.getHeader("Accept");
            boolean useWebp = acceptHeader.contains("image/webp");
            //如果它的地址不是原图像也不是剪切后的图像的话
            if (!width.equalsIgnoreCase("org")&&!numPattern.matcher(width).matches()){
                throw new ImageNotFoundException();
            }

            if (name.contains(".")){
                name = name.substring(0,name.lastIndexOf("."));
            }
            logger.debug(name);
            Optional<Image> optionalImage = repository.findById(name);

            if (optionalImage.isEmpty()){
                throw new ImageNotFoundException();
            }

            Image image = optionalImage.get();
            useWebp = useWebp&&image.getHasWebp();

            if (width.equalsIgnoreCase("org")||!image.getHasConvert()||Integer.parseInt(width)>image.getWidth()){
                return writeImage(service.getImagePath(image),ImageService.extToContentType(image.getExt()));
            }

            if (useWebp) return writeImage(service.getWebpImagePath(image,Integer.valueOf(width)),ImageService.extToContentType("webp"));
            else return writeImage(service.getImagePath(image,Integer.valueOf(width)),ImageService.extToContentType(image.getExt()));
    }

    public ResponseEntity<Resource> writeImage(String path,String contentType) throws IOException,ImageException {
        File file = new File(path);
        if (!file.isFile()){
            throw new ImageNotFoundException();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.valueOf(contentType))
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)))
                .body(resource);
    }
}
