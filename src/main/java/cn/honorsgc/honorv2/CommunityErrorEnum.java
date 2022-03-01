package cn.honorsgc.honorv2;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum CommunityErrorEnum implements ErrorEnum {
    ACCESS_DENIED(40200,"没有权限"),
    NOT_FOUND(40401,"文章不存在"),
    ILLEGAL_PARAMETER(40202,"参数错误");

    private final Integer errorCode;
    private final String errorMsg;

    CommunityErrorEnum(Integer errorCode, String errorMsg) {
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
