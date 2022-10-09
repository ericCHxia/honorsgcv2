package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.expection.PageNotFoundException;
import cn.honorsgc.honorv2.user.dto.UserDto;
import cn.honorsgc.honorv2.user.dto.UserOptionResponseBody;
import cn.honorsgc.honorv2.user.exception.UserException;
import cn.honorsgc.honorv2.user.exception.UserHaveExistException;
import cn.honorsgc.honorv2.user.exception.UserIllegalParameterException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    UserRepository repository;
    @Autowired
    UserService service;
    @Autowired
    UserMapper userMapper;

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

    @GetMapping("/mine")
    @ApiOperation(value = "获取当前用户信息")
    public User getMine(@ApiIgnore Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping({"","/"})
    @Secured({"ROLE_ADMIN"})
    public Page<UserDto> get(@ApiParam(value = "页号") @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
                             @ApiParam(value = "页面大小") @RequestParam(value = "page_size", required = false, defaultValue = "25") Integer pageSize,
                             @ApiParam(value = "班级号") @RequestParam(value = "class", required = false, defaultValue = "") String classId,
                             @ApiParam(value = "学院") @RequestParam(value = "college", required = false, defaultValue = "") String college,
                             @ApiParam(value = "专业") @RequestParam(value = "subject", required = false, defaultValue = "") String subject,
                             @ApiParam(value = "学号") @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
                             @ApiParam(value = "姓名") @RequestParam(value = "name", required = false, defaultValue = "") String name) throws UserException {
        if (pageSize>50)
        {
            throw new UserIllegalParameterException("page_size 应小于等于50");
        }

        Specification<User> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!classId.equals("")) list.add(criteriaBuilder.like(root.get("classId"),"%"+classId+"%"));
            if (!name.equals(""))list.add(criteriaBuilder.like(root.get("name"),"%"+name+"%"));
            if (!college.equals(""))list.add(criteriaBuilder.like(root.get("college"),"%"+college+"%"));
            if (!subject.equals(""))list.add(criteriaBuilder.like(root.get("subject"),"%"+subject+"%"));
            if (!userId.equals(""))list.add(criteriaBuilder.like(root.get("userId"),"%"+userId+"%"));
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
        };

        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<User> pages = repository.findAll(specification,pageable);
        return new PageImpl<>(userMapper.userToUserDto(pages.getContent()), pageable, pages.getTotalElements());
    }

    @GetMapping("/options")
    @Secured({"ROLE_ADMIN"})
    public UserOptionResponseBody getOptions(){
        Set<String> classId = repository.getClassIds();
        Set<String> college = repository.getCollegeNames();
        Set<String> subject = repository.getSubjectNames();
        classId.remove(null);
        college.remove(null);
        subject.remove(null);
        classId.remove("");
        college.remove("");
        subject.remove("");
        return new UserOptionResponseBody(classId,college,subject);
    }

    @PostMapping("/reset-password")
    @Secured({"ROLE_SUPER"})
    public GlobalResponseEntity<String> resetPassword(@ApiParam(value = "用户编号") @RequestParam List<Long> ids){
        List<User> users = repository.findAllById(ids);
        users.forEach(a->a.setPassword(DigestUtils.md5DigestAsHex(a.getUserId().getBytes())));
        repository.saveAll(users);
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("重置成功");
        return responseEntity;
    }

    @PostMapping({"","/"})
    @Secured({"ROLE_SUPER"})
    public GlobalResponseEntity<String> newUsers(@RequestBody User user) throws UserException {
        if (user.getId() == null) {
            if (repository.findUserByUserId(user.getUserId()).isPresent()) throw new UserHaveExistException();
            user.setAvatar("");
            user.setQq("");
        }
        repository.save(user);
        return new GlobalResponseEntity<>();
    }

    @PostMapping("/avatar")
    @ApiOperation("设置头像")
    public User setAva(@ApiIgnore Authentication authentication,
                       @ApiParam(required = true, value = "头像") @RequestParam String avatar) {
        User auth = (User) authentication.getPrincipal();

        auth.setAvatar(avatar);
        return repository.save(auth);
    }
}
