package cn.honorsgc.honorv2.jwt;

import org.springframework.beans.factory.annotation.Value;

public class ConstantKey {
    @Value("${jwt.signing_key}")
    public static String SIGNING_KEY="spring-security-@Jwt!&Secret^#";
}
