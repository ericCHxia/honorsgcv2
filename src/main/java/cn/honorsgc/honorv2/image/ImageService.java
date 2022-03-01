package cn.honorsgc.honorv2.image;

import cn.honorsgc.honorv2.image.exception.ImageException;
import cn.honorsgc.honorv2.image.exception.ImageSizeTooLarge;
import cn.honorsgc.honorv2.image.exception.UnsupportedImageTypeException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageService {
    public static final Integer[] available_width = {200, 400, 600, 800, 1024, 1280, 1366, 1440, 1600, 1920, 2560};
    @Autowired
    private ImageConfig imageConfig;

    @Autowired
    private ImageRepository repository;

//    public static String generateBaseFilename(){
//        return  UUID.randomUUID().toString().replace("-","");
//    }
//
//    public final Set<String> acceptContentTypes = new HashSet<>();
//
//    public ImageService(){
//        acceptContentTypes.addAll(List.of(imageConfig.getAcceptContentTypes()));
//    }
//
//    public boolean checkSize(MultipartFile file){
//        return imageConfig.getMaxSize().toBytes()>=file.getSize();
//    }
//
//    private String preprocess(MultipartFile file)throws ImageException {
//        String filename=file.getOriginalFilename();
//        if (filename==null||filename.lastIndexOf(".")==-1||!acceptContentTypes.contains(file.getContentType())){
//            throw new UnsupportedImageTypeException();
//        }
//        if (!checkSize(file)){
//            throw new ImageSizeTooLarge();
//        }
//        return filename;
//    }
//
//    @Async
//    public void convert(Image image) {
//        String orgImagePath = imageConfig.getPath()+"/org/"+image.getId()+"."+image.getExt();
//        String ext = image.getExt();
//        if (Objects.equals(ext, "webp")){
//            ext = "png";
//        }
//        String format = ext;
//        if (format.equals("jpg")){
//            format = "jpeg";
//        }
//        for (Integer maxWidth: available_width) {
//            try {
//                Thumbnails.of(orgImagePath)
//                        .width(maxWidth)
//                        .outputQuality(imageConfig.getSaveQuality())
//                        .outputFormat(format).toFile(imageConfig.getPath()+"/"+ maxWidth +"/"+image.getId()+"."+ext);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Async
//    public void convertWebp(Image image) {
//        String orgImagePath = imageConfig.getPath()+"/org/"+image.getId()+"."+image.getExt();
//        for (Integer maxWidth: available_width) {
//            try {
//                Thumbnails.of(orgImagePath)
//                        .width(maxWidth)
//                        .outputQuality(imageConfig.getSaveQuality())
//                        .outputFormat("webp").toFile(imageConfig.getPath()+"/webp/"+ maxWidth +"/"+image.getId()+".webp");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public String save(MultipartFile file) throws ImageException{
//        String filename=preprocess(file);
//        String suffixName=filename.substring(filename.lastIndexOf("."));
//        filename = UUID.randomUUID().toString().replace("-","")+suffixName;
//        try {
//            file.transferTo(new File(imageConfig.getPath(),filename));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return filename;
//    }
}
