package cn.honorsgc.honorv2.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTTokenResponse implements Serializable {
    private String token;
}
