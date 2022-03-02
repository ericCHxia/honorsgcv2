package cn.honorsgc.honorv2.image;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class ImageSrcsetSingle implements Serializable {
    private final String url;
    private final Integer width;
    public static ImageSrcsetSingle valuesOf(String url,Integer width){
        return new ImageSrcsetSingle(url,width);
    }
    public static ImageSrcsetSingle valuesOf(Image image){
        String path = "/image/org/"+image.getId()+"."+image.getExt();
        return new ImageSrcsetSingle(path,image.getWidth());
    }
    public static ImageSrcsetSingle valuesOf(Image image,Integer width){
        String path = "/image/"+width+"/"+image.getId()+"."+image.getExt();
        return new ImageSrcsetSingle(path,width);
    }
}
