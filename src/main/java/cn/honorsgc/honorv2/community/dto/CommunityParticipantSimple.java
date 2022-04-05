package cn.honorsgc.honorv2.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommunityParticipantSimple implements Serializable {
    private final Long id;
    private final String name;
    private final String classID;
    private final String userID;
    private final String major;
    private final String college;
    private final String qq;
    private final String phone;
    private final Integer type;
    private final Boolean valid;
    private final String avatar;
}
