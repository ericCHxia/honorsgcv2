package cn.honorsgc.honorv2.community.mapper;

import cn.honorsgc.honorv2.community.convert.CommunityConvert;
import cn.honorsgc.honorv2.community.dto.*;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import cn.honorsgc.honorv2.image.ImageConvert;
import cn.honorsgc.honorv2.user.UserConvert;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {CommunityConvert.class, ImageConvert.class, UserConvert.class})
public interface CommunityMapper {
    CommunitySaveResponseBody communityToCommunitySaveResponseBody(Community community);

    @Mapping(source = "state", target = "state")
    @Mapping(source = "typeId", target = "type")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCommunityFromCommunityRequestBody(CommunityRequestBody communityRequestBody, @MappingTarget Community community);

    @Mapping(source = "participantsCount", target = "participants")
    CommunitySimple communityToCommunitySimple(Community community);

    List<CommunitySimple> communityToCommunitySimple(List<Community> community);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "classID", source = "user.classId")
    @Mapping(target = "userID", source = "user.userId")
    @Mapping(target = "major", source = "user.subject")
    @Mapping(target = "college", source = "user.college")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "qq", source = "user.qq")
    @Mapping(target = "avatar", source = "user.avatar")
    CommunityParticipantSimple communityParticipantToCommunityParticipantSimple(CommunityParticipant communityParticipant);

    Collection<CommunityParticipantSimple> communityParticipantToCommunityParticipantSimple(Collection<CommunityParticipant> communityParticipant);

    CommunityParticipantResponse communityToCommunityParticipantResponse(Community community);

    CommunityDetail communityToCommunityDetail(Community community);

    @Mapping(target = "image", source = "cover")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "community.title", target = "communityTitle")
    CommunityRecordDto communityRecordToCommunityRecordDto(CommunityRecord communityRecord);

    List<CommunityRecordDto> communityRecordToCommunityRecordDto(List<CommunityRecord> communityRecord);

    @Mapping(source = "communityId", target = "community")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCommunityRecordFromCommunityRecordRequestBody(CommunityRecordRequestBody communityRecordRequestBody, @MappingTarget CommunityRecord communityRecord);
}
