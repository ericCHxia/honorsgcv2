package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.community.entity.CommunityTypeDto;
import cn.honorsgc.honorv2.image.ImageResponse;
import cn.honorsgc.honorv2.user.dto.UserSimple;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommunitySimple implements Serializable {
    private final Long id;
    private final UserSimple user;
    private final CommunityTypeDto type;
    private final String title;
    private final String describe;
    private final ImageResponse img;
    private final Integer limit;
    private final Integer state;
    private final Date createDate;
    private final Boolean enrolling;
    private final Integer participants;
    private final Integer registrationType;
}
