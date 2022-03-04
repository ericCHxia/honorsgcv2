package cn.honorsgc.honorv2.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CommunityRecordResponseBody implements Serializable {
    private final Long communityId;
    private final Integer count;
    private final List<CommunityRecordDto> records;
    public static CommunityRecordResponseBody valuesOf(Long communityId,List<CommunityRecordDto> records){
        return new CommunityRecordResponseBody(communityId,records.size(),records);
    }
}
