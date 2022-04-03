package cn.honorsgc.honorv2.hduhelper;

import cn.honorsgc.honorv2.hduhelper.dto.HduHelperPersonInfo;
import cn.honorsgc.honorv2.hduhelper.dto.HduHelperStudentInfo;
import cn.honorsgc.honorv2.user.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface HduHelperMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "staffId", target = "userId")
    @Mapping(source = "staffName", target = "name")
    @Mapping(source = "majorName", target = "subject")
    @Mapping(source = "unitName", target = "college")
    void UserUpdateFromStudentInfo(HduHelperStudentInfo userInfo, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "staffId", target = "userId")
    @Mapping(source = "staffName", target = "name")
    void UserUpdateFromPersonInfo(HduHelperPersonInfo personInfo, @MappingTarget User user);
}