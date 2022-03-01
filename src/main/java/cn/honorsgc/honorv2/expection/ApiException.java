package cn.honorsgc.honorv2.expection;

import cn.honorsgc.honorv2.ResultInfo;

public class ApiException extends RuntimeException{
    int code;
    public ApiException(ResultInfo info){
        super(info.getMessage());
        code = info.getCode();
    }
}
