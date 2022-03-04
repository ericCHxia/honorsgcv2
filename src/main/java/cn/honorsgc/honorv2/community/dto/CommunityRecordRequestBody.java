package cn.honorsgc.honorv2.community.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class CommunityRecordRequestBody implements Serializable {
    private final Long user;
    private final Long communityId;
    @NotNull
    private final String detail;
    private final String cover;
    private final List<Long> attendant;
}
