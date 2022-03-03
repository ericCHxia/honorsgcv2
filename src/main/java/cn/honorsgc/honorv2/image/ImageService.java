package cn.honorsgc.honorv2.image;

import cn.honorsgc.honorv2.image.exception.ImageException;
import cn.honorsgc.honorv2.image.exception.ImageNotFoundException;
import cn.honorsgc.honorv2.image.exception.ImageSizeTooLarge;
import cn.honorsgc.honorv2.image.exception.UnsupportedImageTypeException;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    public static final Integer[] available_width = {200, 400, 600, 800, 1024, 1280, 1366, 1440, 1600, 1920, 2560};
    public static final Set<Integer>  available_width_set = new HashSet<>(Arrays.asList(available_width));

    private static final Set<String> noChangeSet = new HashSet<>(Arrays.asList("png","jpeg","gif","webp","avif"));
    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private ImageRepository repository;

    public static String generateBaseFilename(){
        return  UUID.randomUUID().toString().replace("-","");
    }

    public String getImagePath(Image image,Integer width)throws ImageException{
        if (!available_width_set.contains(width)||width>image.getWidth()){
            throw new ImageNotFoundException();
        }

        return imageConfig.getPath()+File.separatorChar+width+File.separatorChar+image.getId()+"."+image.getExt();
    }

    public String getImagePath(Image image) {
        return imageConfig.getPath()+File.separatorChar+"org"+File.separatorChar+image.getId()+"."+image.getExt();
    }

    public String getWebpImagePath(Image image,Integer width) throws ImageException{
        if (!available_width_set.contains(width)||width>image.getWidth()){
            throw new ImageNotFoundException();
        }

        return imageConfig.getPath()+File.separatorChar+"webp"+File.separatorChar+width+File.separatorChar+image.getId()+"."+"webp";
    }

    public final Set<String> acceptContentTypes = new HashSet<>();

    public ImageService(ImageConfig imageConfig,ImageRepository repository){
        this.imageConfig = imageConfig;
        this.repository = repository;
        acceptContentTypes.addAll(List.of(imageConfig.getAcceptContentTypes()));
    }

    public boolean checkSize(MultipartFile file){
        return imageConfig.getMaxSize().toBytes()>=file.getSize();
    }

    private String preprocess(MultipartFile file)throws ImageException {
        String filename=file.getOriginalFilename();
        if (filename==null||filename.lastIndexOf(".")==-1||!acceptContentTypes.contains(file.getContentType())){
            throw new UnsupportedImageTypeException();
        }
        if (!checkSize(file)){
            throw new ImageSizeTooLarge();
        }
        return filename;
    }

    public static String extToContentType(String ext)throws ImageException{
        if (noChangeSet.contains(ext.toLowerCase(Locale.ROOT))){
            return "image/"+ext;
        }
        if (ext.equalsIgnoreCase("jpg")){
            return extToContentType("jpeg");
        }
        logger.info(ext);
        throw new UnsupportedImageTypeException();
    }

    @Async
    public void convert(Image image) {
        String orgImagePath = imageConfig.getPath()+File.separatorChar+"org"+File.separatorChar+image.getId()+"."+image.getExt();
        String ext = image.getExt();
        if (Objects.equals(ext, "webp")){
            ext = "png";
        }
        String format = ext;
        if (format.equals("jpg")){
            format = "jpeg";
        }
        for (Integer maxWidth: available_width) {
            if (maxWidth>image.getWidth())break;
            try {
                Thumbnails.of(orgImagePath)
                        .width(maxWidth)
                        .outputQuality(imageConfig.getSaveQuality())
                        .outputFormat(format).toFile(imageConfig.getPath()+"/"+ maxWidth +"/"+image.getId()+"."+ext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        image.setHasConvert(true);
        repository.save(image);
    }

    @Async
    public void convertWebp(Image image) {
        String orgImagePath = imageConfig.getPath()+"/org/"+image.getId()+"."+image.getExt();
        for (Integer maxWidth: available_width) {
            if (maxWidth>image.getWidth())break;
            try {
                Thumbnails.of(orgImagePath)
                        .width(maxWidth)
                        .outputQuality(imageConfig.getSaveQuality())
                        .outputFormat("webp").toFile(imageConfig.getPath()+"/webp/"+ maxWidth +"/"+image.getId()+".webp");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        image.setHasWebp(true);
        repository.save(image);
    }

    public Image save(MultipartFile file) throws ImageException{

        String filename=preprocess(file);
        Image image = new Image();
        logger.info(image.getId());
        String suffixName=filename.substring(filename.lastIndexOf("."));
        suffixName = suffixName.toLowerCase(Locale.ROOT);

        image.setExt(suffixName.substring(1));

        String orgImagePath = imageConfig.getPath()+"/org/"+image.getId()+"."+image.getExt();

        String ext=suffixName.substring(1);
        if (ext.equals("jpg")){
            ext = "jpeg";
        }

        try {
            file.transferTo(new File(orgImagePath));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(orgImagePath).width(50).toOutputStream(outputStream);

            BufferedImage bufferedImage = ImageIO.read(new File(orgImagePath));
            image.setWidth(bufferedImage.getWidth());
            image.setHeight(bufferedImage.getHeight());
            String base64Img = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            base64Img = String.format("data:image/%s;base64,%s",ext,base64Img);
            image.setBase64(base64Img);
        }catch (Exception e){
            e.printStackTrace();
        }
        image = repository.save(image);
        return image;
    }
}
