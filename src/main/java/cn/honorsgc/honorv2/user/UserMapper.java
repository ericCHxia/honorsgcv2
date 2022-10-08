package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.user.dto.UserDto;
import cn.honorsgc.honorv2.user.dto.UserSimple;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
    List<UserDto> userToUserDto(List<User> user);
    UserSimple userToUserSimple(User user);
}
