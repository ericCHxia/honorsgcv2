package cn.honorsgc.honorv2.image;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum ImageErrorNum implements ErrorEnum {
    IMAGE_NOT_FOUND_EXCEPTION(40400,"照片未找到"),
    IMAGE_SIZE_TOO_LARGE(40201,"照片过大"),
    UNSUPPORTED_IMAGE_TYPE(40302,"不支持的照片格式");
    private final Integer errorCode;
    private final String errorMsg;

    ImageErrorNum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public GlobalResponseEntity<String> responseEntity(){
        return new GlobalResponseEntity<>(errorCode,errorMsg);
    }

    @Override
    public int getStatus() {
        return errorCode/100;
    }
}
