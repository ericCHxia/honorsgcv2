package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.hduhelper.dto.*;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperException;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperGetTokenException;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperGetUserInfoException;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperTokenExpiredException;
import cn.honorsgc.honorv2.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class HduHelperService {
    private static final Logger logger = LoggerFactory.getLogger(HduHelperService.class);
    @Autowired
    private HduHelperConfig hduHelperConfig;
    @Autowired
    private UserService userService;

    public HduHelperToken getToken(String code) throws HduHelperException {
        logger.debug("TOKEN: "+code);
        Map<String,String> map = new HashMap<>();
        map.put("grant_type","authorization_code");
        logger.debug("TOKEN ID: "+hduHelperConfig.getId());
        map.put("client_id",hduHelperConfig.getId());
        map.put("client_secret",hduHelperConfig.getSecret());
        map.put("code",code);

        RestTemplate restTemplate = new RestTemplate();
        HduHelperTokenResponse response;
        try {
            response = restTemplate.getForObject(hduHelperConfig.baseUrl+"/oauth/token?grant_type={grant_type}&client_id={client_id}&client_secret={client_secret}&code={code}",
                    HduHelperTokenResponse.class,map);
        }catch (HttpClientErrorException e){
            throw new HduHelperGetTokenException("Invalid code");
        }

        if (response==null){
            logger.debug("TOKEN: "+"response is None");
            throw new HduHelperGetTokenException("response is None");
        }
        if (response.getError()!=0){
            logger.debug("TOKEN: "+response.getMsg());
            throw new HduHelperGetTokenException(response.getMsg());
        }
        HduHelperToken token = response.getData();
        if (Instant.now().isAfter(Instant.ofEpochSecond(token.getAccessTokenExpire()))){
            logger.debug("TOKEN: AccessTokenExpire");
            throw new HduHelperTokenExpiredException();
        }
        return response.getData();
    }

    public HduHelperUserInfo getUserInfo(String token)throws HduHelperException{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","token "+token);
        ResponseEntity<HduHelperUserInfoResponse> response = restTemplate.exchange("https://api.hduhelp.com/base/student/info", HttpMethod.GET,new HttpEntity<String>(headers),HduHelperUserInfoResponse.class);
        if (response.getStatusCode()!= HttpStatus.OK||response.getBody()==null){
            logger.debug("INFO: "+ Objects.requireNonNull(response.getBody()).getMsg());
            throw new HduHelperGetUserInfoException();
        }
        return response.getBody().getData();
    }
}
