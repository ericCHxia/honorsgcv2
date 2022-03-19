package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperGetTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HduHelperHandle {
    @ExceptionHandler(HduHelperGetTokenException.class)
    public ErrorEnum hduHelperGetToken(HduHelperGetTokenException exception){
        return HduHelperErrorEnum.GET_TOKEN_ERROR.setErrorMsg(exception.getMessage());
    }
}
