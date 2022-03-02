package cn.honorsgc.honorv2.image;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ImageResponse {
    private final ImageSrcsetSingle original;
    private final Integer width;
    private final Integer height;
    private final ImageSrcsetSingle[] srcset;
    private final String base64;

    public static ImageResponse valuesOf(Image image){
        ImageSrcsetSingle original = ImageSrcsetSingle.valuesOf(image);
        Integer width = image.getWidth();
        Integer height = image.getHeight();
        List<ImageSrcsetSingle> srcset = new ArrayList<>();
        srcset.add(original);
        for (Integer maxWidth:ImageService.available_width){
            if (width<maxWidth){
                break;
            }
            srcset.add(ImageSrcsetSingle.valuesOf(image,maxWidth));
        }
        return new ImageResponse(original,width,height,srcset.toArray(new ImageSrcsetSingle[0]),image.getBase64());
    }
}
