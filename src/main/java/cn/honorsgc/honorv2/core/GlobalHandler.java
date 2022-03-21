package cn.honorsgc.honorv2.core;

import cn.honorsgc.honorv2.expection.ApiException;
import cn.honorsgc.honorv2.expection.PageNotFoundException;
import cn.honorsgc.honorv2.jwt.JWTErrorEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(PageNotFoundException.class)
    public Object pageNotFoundHandler(HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new GlobalResponseEntity<>(404,"资源不存在");
    }
    @ExceptionHandler(ApiException.class)
    public Object apiExceptionHandler(HttpServletResponse response, ApiException exception){
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new GlobalResponseEntity<>(404,exception.getMessage());
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    public Object expiredJwtException(HttpServletResponse response) {
        response.setStatus(JWTErrorEnum.expired.getStatus());
        return JWTErrorEnum.expired.responseEntity();
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public Object unsupportedJwtException(HttpServletResponse response) {
        response.setStatus(JWTErrorEnum.unsupported.getStatus());
        return JWTErrorEnum.unsupported.responseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorEnum methodArgumentTypeMismatchException(){
        return CoreErrorEnum.PAGE_NOT_FOUND;
    }
}
