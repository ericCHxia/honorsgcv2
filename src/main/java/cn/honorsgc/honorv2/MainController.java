package cn.honorsgc.honorv2;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.jwt.JWTErrorEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    @RequestMapping("/exception")
    public void error(HttpServletRequest request) throws Exception {
        throw (Exception) request.getAttribute("Exception");
    }
    @RequestMapping("/expiredJwtException")
    public ErrorEnum expiredJwtException(HttpServletRequest request) throws ExpiredJwtException {
        return JWTErrorEnum.expired;
    }
    @RequestMapping("/unsupportedJwtException")
    public void unsupportedJwtException(HttpServletRequest request) throws UnsupportedJwtException {
        if (request.getAttribute("unsupportedJwtException") instanceof ExpiredJwtException) {
            throw ((UnsupportedJwtException) request.getAttribute("unsupportedJwtException"));
        }
    }
}
