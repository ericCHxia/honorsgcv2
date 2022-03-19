package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.ResultInfo;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.hduhelper.dto.HduHelperToken;
import cn.honorsgc.honorv2.hduhelper.dto.HduHelperUserInfo;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperException;
import cn.honorsgc.honorv2.jwt.JWTHelper;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class HduHelperController {
    @Autowired
    private HduHelperService service;
    @Autowired
    private UserRepository repository;
    @Autowired
    private HduHelperMapper mapper;
    @Autowired
    private JWTHelper jwtHelper;
    @GetMapping("/hduhelper")
    public void oauth2login(@RequestParam(name = "code") String code, HttpServletResponse response) throws HduHelperException, IOException {
        HduHelperToken token = service.getToken(code);
        HduHelperUserInfo userInfo = service.getUserInfo(token.getAccessToken());
        Optional<User> optionalUser = repository.findUserByUserId(userInfo.getStaffId());
        User user;
        if (optionalUser.isEmpty()){
            user = new User();
            user.setPrivilege(0);
            user.setAvatar("");
            user.setQq("");
            user.setPassword("");
        }else {
            user = optionalUser.get();
        }
        mapper.UserUpdateFromUserInfo(userInfo,user);
        repository.save(user);
        String jwtToken = jwtHelper.generateToken(user);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", jwtToken);
        response.getWriter().write(JSON.toJSONString(new GlobalResponseEntity<>(ResultInfo.ok(),resultMap)));
    }
}
