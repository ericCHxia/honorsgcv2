package cn.honorsgc.honorv2.user.dto;

import cn.honorsgc.honorv2.core.GlobalAuthority;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class UserDto implements Serializable {
    private final Long id;
    private final String name;
    private final String classId;
    private final String userId;
    private final String subject;
    private final String college;
    private final String qq;
    private final String avatar;
    private final String phone;
    private final int privilege;
    private final Collection<GlobalAuthority> authorities;
}
