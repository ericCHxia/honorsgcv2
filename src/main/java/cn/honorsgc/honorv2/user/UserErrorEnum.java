package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum UserErrorEnum {
    passwordError(40200,"密码错误")
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
