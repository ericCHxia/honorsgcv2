package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.expection.PageNotFoundException;
import cn.honorsgc.honorv2.user.exception.UserException;
import cn.honorsgc.honorv2.user.exception.UserIllegalParameterException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    final private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserRepository repository;
    @Autowired
    UserService service;

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "获取用户信息")
    public User get(@ApiParam(required = true, value = "用户编号") @PathVariable Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new PageNotFoundException();
        }
        return user.get();
    }

    @ApiOperation(value = "修改用户密码")
    @PostMapping("/password")
    public GlobalResponseEntity<String> changePassword(@ApiIgnore Authentication authentication,
                                                       HttpServletResponse response,
                                                       @ApiParam(required = true, value = "旧密码") @RequestParam String oldPassword,
                                                       @ApiParam(required = true, value = "新密码") @RequestParam String newPassword) {
        User user = (User) authentication.getPrincipal();
        if (user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            user = repository.saveAndFlush(user);
            UserService.flushUser(user);
            return new GlobalResponseEntity<>("修改成功");
        } else {
            response.setStatus(UserErrorEnum.passwordError.getStatus());
            return UserErrorEnum.passwordError.responseEntity();
        }
    }

    @GetMapping({"", "/"})
    @ApiOperation(value = "获取当前用户信息")
    public User get(@ApiIgnore Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @PostMapping("ava")
    @ApiOperation("设置头像")
    public User setAva(@ApiIgnore Authentication authentication,
                       @ApiParam(required = true, value = "头像") @RequestParam String ava,
                       @ApiParam(required = true, value = "用户编号") @RequestParam Long id) throws UserException {
        Optional<User> optionalUser = repository.findById(id);
        if(optionalUser.isEmpty()){
            throw new UserIllegalParameterException("用户不存在");
        }
        User user = optionalUser.get();
        User auth = (User) authentication.getPrincipal();
        if(!auth.equals(user)){
            throw new UserIllegalParameterException("您不是本人");
        }

        user.setAvatar(ava);
        return repository.save(user);
    }
}
