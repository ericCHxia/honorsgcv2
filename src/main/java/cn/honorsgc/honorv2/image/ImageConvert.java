package cn.honorsgc.honorv2.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ImageConvert {
    @Autowired
    private ImageRepository repository;

    public Image filenameToImage(String filename) {
        filename = filename.substring(0,filename.lastIndexOf('.'));
        Optional<Image> image = repository.findById(filename);
        return image.orElse(null);
    }

    public ImageResponse filenameToImageResponse(String filename) {
        filename = filename.substring(0,filename.lastIndexOf('.'));
        Optional<Image> optionalImage = repository.findById(filename);
        if (optionalImage.isEmpty())return null;
        Image image = optionalImage.get();
        return ImageResponse.valuesOf(image);
    }
}
