package cn.honorsgc.honorv2.hduhelper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HduHelperToken implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("access_token_expire")
    private long accessTokenExpire;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_token_expire")
    private long refreshTokenExpire;
    @JsonProperty("staff_id")
    private String staffId;
}
