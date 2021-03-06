package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;

public enum ArticleErrorEnum implements ErrorEnum {
    ACCESS_DENIED(40200,"没有权限"),
    NOT_FOUND(40401,"文章不存在"),
    ILLEGAL_PARAMETER(40202,"参数错误"),
    TAG_IS_EXIST(40303,"标签已经存在"),
    TAG_COUNT_IS_NOT_EMPTY(40304,"标签引用数不为0")
    ;
    private final Integer errorCode;
    private final String errorMsg;

    ArticleErrorEnum(Integer errorCode, String errorMsg) {
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
