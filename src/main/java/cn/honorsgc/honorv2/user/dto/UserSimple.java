package cn.honorsgc.honorv2.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSimple implements Serializable {
    private final Long id;
    private final String name;
    private final String userId;
    private final String subject;
    private final String avatar;
}
