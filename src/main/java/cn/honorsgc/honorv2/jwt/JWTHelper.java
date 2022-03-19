package cn.honorsgc.honorv2.jwt;

import cn.honorsgc.honorv2.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JWTHelper {

    //token超时时间
    @Value("${jwt.expiration}")
    private int expiration;

    //生成token的秘钥
    @Value("${jwt.secret}")
    private String base64Security;

    public String generateToken(Claims claims) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        // 设置签发时间
        calendar.setTime(new Date());
        // 设置过期时间
        calendar.add(Calendar.SECOND, expiration);// 5分钟
        Date time = calendar.getTime();
        JwtBuilder builder = Jwts.builder()
                .setSubject(claims.getSubject())
                .setIssuedAt(now)//签发时间
                .setExpiration(time)//过期时间
                .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY);
        return builder.compact();
    }

    public String generateToken(User user) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        // 设置签发时间
        calendar.setTime(new Date());
        // 设置过期时间
        calendar.add(Calendar.SECOND, expiration);// 5分钟
        Date time = calendar.getTime();
        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)//签发时间
                .setExpiration(time)//过期时间
                .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY);
        return builder.compact();
    }
}