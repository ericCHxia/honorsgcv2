package cn.honorsgc.honorv2.community.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class CommunityRecordRequestBody implements Serializable {
    private  Long communityId;
    @NotNull
    private  String detail;
    private  String cover;
    private  List<Long> attendant;
}
