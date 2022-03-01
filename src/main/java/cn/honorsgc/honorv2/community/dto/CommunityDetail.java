package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.community.entity.CommunityTypeDto;
import cn.honorsgc.honorv2.user.dto.UserSimple;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
public class CommunityDetail implements Serializable {
    private final Long id;
    private final UserSimple user;
    private final CommunityTypeDto type;
    private final String title;
    private final String describe;
    private final String detail;
    private final String img;
    private final Integer limit;
    private final Integer state;
    private final Date createDate;
    private final Boolean enrolling;
    private final Set<CommunityParticipantSimple> participants;
    private final Set<CommunityParticipantSimple> mentors;
}
