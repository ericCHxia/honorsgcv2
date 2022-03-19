package cn.honorsgc.honorv2.image;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.image.exception.ImageNotFoundException;
import cn.honorsgc.honorv2.image.exception.ImageSizeTooLarge;
import cn.honorsgc.honorv2.image.exception.UnsupportedImageTypeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ImageExceptionHandle {
    @ExceptionHandler(ImageNotFoundException.class)
    public ErrorEnum imageNotFoundExceptionHandle(){
        return ImageErrorNum.IMAGE_NOT_FOUND_EXCEPTION;
    }
    @ExceptionHandler(ImageSizeTooLarge.class)
    public ErrorEnum imageSizeTooLarge(){
        return ImageErrorNum.IMAGE_SIZE_TOO_LARGE;
    }
    @ExceptionHandler(UnsupportedImageTypeException.class)
    public ErrorEnum unsupportedImageTypeException(){
        return ImageErrorNum.UNSUPPORTED_IMAGE_TYPE;
    }
}
