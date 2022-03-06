package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum UserErrorEnum implements ErrorEnum {
    passwordError(40200,"密码错误"),
    ILLEGAL_PARAMETER(40202,"参数错误"),
    ;
    private final Integer errorCode;
    private final String errorMsg;
    public GlobalResponseEntity<String> responseEntity(){
        return new GlobalResponseEntity<>(errorCode,errorMsg);
    }

    UserErrorEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getStatus() {
        return errorCode/100;
    }
}
