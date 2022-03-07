package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.image.ImageResponse;
import cn.honorsgc.honorv2.user.dto.UserSimple;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CommunityRecordDto implements Serializable {
    private final Integer id;
    private final UserSimple user;
    private final Long communityId;
    private final String communityTitle;
    @NotNull
    private final String detail;
    private final ImageResponse image;
    private final Date createTime;
    private final List<UserSimple> attendant;
}
