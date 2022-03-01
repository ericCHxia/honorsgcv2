package cn.honorsgc.honorv2.community.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class CommunityParticipantResponse implements Serializable {
    @ApiModelProperty("共同体编号")
    private final Long id;
    @ApiModelProperty("参与者")
    private final Set<CommunityParticipantSimple> participants;
    @ApiModelProperty("指导学长")
    private final Set<CommunityParticipantSimple> mentors;
}
