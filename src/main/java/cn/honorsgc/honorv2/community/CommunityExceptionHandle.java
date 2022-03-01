package cn.honorsgc.honorv2.community;

import cn.honorsgc.honorv2.CommunityErrorEnum;
import cn.honorsgc.honorv2.community.exception.CommunityAccessDenied;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.exception.CommunityNotFoundException;
import cn.honorsgc.honorv2.core.ErrorEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommunityExceptionHandle {
    @ExceptionHandler(CommunityAccessDenied.class)
    public ErrorEnum communityAccessDenied(){
        return CommunityErrorEnum.ACCESS_DENIED;
    }
    @ExceptionHandler(CommunityIllegalParameterException.class)
    public ErrorEnum illegalParameter(CommunityIllegalParameterException exception){
        return CommunityErrorEnum.ILLEGAL_PARAMETER.setErrorMsg(exception.getMessage());
    }
    @ExceptionHandler(CommunityNotFoundException.class)
    public ErrorEnum notFound(){
        return CommunityErrorEnum.NOT_FOUND;
    }
}
