package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.hduhelper.dto.*;
import cn.honorsgc.honorv2.hduhelper.exception.HduHelperException;
import cn.honorsgc.honorv2.jwt.JWTHelper;
import cn.honorsgc.honorv2.jwt.JWTTokenResponse;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
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
    public JWTTokenResponse oauth2login(@RequestParam(name = "code") String code, HttpServletResponse response) throws HduHelperException {
        HduHelperToken      token        = service.getToken(code);
        Optional<User>      optionalUser = repository.findUserByUserId(token.getStaffId());
        User user;
        if (optionalUser.isEmpty()) {
            user = new User();
            user.setPrivilege(0);
            user.setAvatar("");
            user.setQq("");
            user.setPassword("");
        } else {
            user = optionalUser.get();
        }

        HduHelperPersonInfo personInfo   = service.getPersonInfo(token.getAccessToken());
        HduHelperPhoneInfo  phoneInfo    = service.getPhoneInfo(token.getAccessToken());

        user.setPhone(phoneInfo.getPhone());
        log.info(phoneInfo.getPhone());
        mapper.UserUpdateFromPersonInfo(personInfo, user);
        if (personInfo.getStaffType().equals(HduHelperPersonInfo.StudentType)) {
            HduHelperStudentInfo studentInfoInfo = service.getStudentInfo(token.getAccessToken());
            mapper.UserUpdateFromStudentInfo(studentInfoInfo, user);
        }
        user = repository.save(user);
        String jwtToken = jwtHelper.generateToken(user);
        return new JWTTokenResponse(jwtToken);
    }
}
