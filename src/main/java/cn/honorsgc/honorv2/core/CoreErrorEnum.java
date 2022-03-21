package cn.honorsgc.honorv2.core;

public enum CoreErrorEnum implements ErrorEnum{
    PAGE_NOT_FOUND(40400,"页面无法找到");
    private final Integer errorCode;
    private final String errorMsg;

    CoreErrorEnum(Integer errorCode, String errorMsg) {
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
