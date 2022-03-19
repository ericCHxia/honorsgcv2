package cn.honorsgc.honorv2;

import cn.honorsgc.honorv2.core.ErrorEnum;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.jwt.JWTErrorEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    @Autowired
    private HonorConfigRepository configRepository;
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
    @GetMapping("/config")
    @Secured({"ROLE_ADMIN"})
    public HonorConfig getConfig(){
        return configRepository.findAll().get(0);
    }

    @PutMapping("/config")
    @Secured({"ROLE_ADMIN"})
    public GlobalResponseEntity<String> updateConfig(@RequestBody HonorConfig config){
        configRepository.deleteAll();
        configRepository.save(config);
        return new GlobalResponseEntity<>("Ok");
    }
}
