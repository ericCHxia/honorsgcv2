package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum HduHelperErrorEnum implements ErrorEnum {
    GET_TOKEN_ERROR(40200,"获取TOKEN错误"),
    ILLEGAL_PARAMETER(40202,"参数错误"),
            ;
    private final Integer errorCode;
    private final String errorMsg;
    public GlobalResponseEntity<String> responseEntity(){
        return new GlobalResponseEntity<>(errorCode,errorMsg);
    }

    HduHelperErrorEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return errorCode/100;
    }
}
