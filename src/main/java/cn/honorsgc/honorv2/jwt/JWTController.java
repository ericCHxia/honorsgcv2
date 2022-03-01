package cn.honorsgc.honorv2.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JWTController {
    final private Logger logger= LoggerFactory.getLogger(JWTController.class);
    @GetMapping("/refresh_token")
    public String refreshToken(@RequestHeader("Authorization") String token){
        logger.info("refreshing");
        Claims claims = Jwts.parser().setSigningKey(ConstantKey.SIGNING_KEY).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        return JWTHelper.refreshToken(claims);
    }
}
