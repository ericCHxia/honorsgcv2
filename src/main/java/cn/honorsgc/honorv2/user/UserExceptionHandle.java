package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.user.exception.UserHaveExistException;
import cn.honorsgc.honorv2.user.exception.UserIllegalParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandle {
    @ExceptionHandler(UserIllegalParameterException.class)
    public ErrorEnum illegalParameter(UserIllegalParameterException exception){
        return UserErrorEnum.ILLEGAL_PARAMETER.setErrorMsg(exception.getMessage());
    }
    @ExceptionHandler(UserHaveExistException.class)
    public ErrorEnum haveExist(){
        return UserErrorEnum.HAVE_EXIST;
    }
}
