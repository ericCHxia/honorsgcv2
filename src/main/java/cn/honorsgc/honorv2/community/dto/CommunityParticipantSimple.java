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
    private final Integer type;
}
