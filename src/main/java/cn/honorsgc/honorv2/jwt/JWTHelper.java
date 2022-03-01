package cn.honorsgc.honorv2.jwt;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTHelper {

    //token超时时间
    @Value("${jwt.expiration}")
    public static long expiration;

    //生成token的秘钥
    @Value("${jwt.secret}")
    public static String base64Security;

    /**
     * 解析token
     * @param jsonWebToken
     * @return
     */
    public Claims parseToken(String jsonWebToken) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(base64Security)).parseClaimsJws(jsonWebToken).getBody();
    }

    /**
     * 新建token
     * @param audience
     * @param issuer
     * @return
     */
    public String createToken(String audience, String issuer) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT").setIssuer(issuer).setAudience(audience)
                .signWith(signatureAlgorithm, signingKey);

        // 添加Token签发时间
        builder.setIssuedAt(now);
        // 添加Token过期时间
        if (expiration >= 0) {
            long expMillis = nowMillis + expiration;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        // 生成JWT
        return builder.compact();
    }

    public static String refreshToken(Claims claims) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        // 设置签发时间
        calendar.setTime(new Date());
        // 设置过期时间
        calendar.add(Calendar.MINUTE, 5);// 5分钟
        Date time = calendar.getTime();
        JwtBuilder builder = Jwts.builder()
                .setSubject(claims.getSubject())
                .setIssuedAt(now)//签发时间
                .setExpiration(time)//过期时间
                .signWith(SignatureAlgorithm.HS512, ConstantKey.SIGNING_KEY);
        return builder.compact();
    }
}