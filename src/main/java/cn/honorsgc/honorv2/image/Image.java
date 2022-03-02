package cn.honorsgc.honorv2.image;

import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "image")
@AllArgsConstructor
public class Image {
    @Id
    @Column(name = "filename", nullable = false, length = 32)
    private String id;

    @Column(name = "ext", nullable = false, length = 4)
    private String ext;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Lob
    @Column(name = "base64")
    private String base64;

    @Column(name = "has_convert")
    private Boolean hasConvert;

    @Column(name = "webp")
    private Boolean hasWebp;

    public Boolean getHasConvert() {
        return hasConvert;
    }

    public void setHasConvert(Boolean hasConvert) {
        this.hasConvert = hasConvert;
    }

    public Boolean getHasWebp() {
        return hasWebp;
    }

    public void setHasWebp(Boolean hasWebp) {
        this.hasWebp = hasWebp;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image() {
        this.id = ImageService.generateBaseFilename();
        this.hasConvert = false;
        this.hasWebp = false;
    }

    public Image(String ext, Integer width, Integer height, String base64) {
        this.id = ImageService.generateBaseFilename();
        this.ext = ext;
        this.width = width;
        this.height = height;
        this.base64 = base64;
        this.hasConvert = false;
        this.hasWebp = false;
    }
}