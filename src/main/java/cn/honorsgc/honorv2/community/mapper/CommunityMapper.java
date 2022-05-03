package cn.honorsgc.honorv2.community.mapper;

import cn.honorsgc.honorv2.community.convert.CommunityConvert;
import cn.honorsgc.honorv2.community.dto.*;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import cn.honorsgc.honorv2.community.repository.CommunityAttendRepository;
import cn.honorsgc.honorv2.image.ImageConvert;
import cn.honorsgc.honorv2.user.UserConvert;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {CommunityConvert.class, ImageConvert.class, UserConvert.class})
public abstract class CommunityMapper {
    @Autowired
    CommunityAttendRepository attendRepository;
    public abstract CommunitySaveResponseBody communityToCommunitySaveResponseBody(Community community);

    @Mapping(source = "state", target = "state")
    @Mapping(source = "typeId", target = "type")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCommunityFromCommunityRequestBody(CommunityRequestBody communityRequestBody, @MappingTarget Community community);

    @Mapping(source = "participantsCount", target = "participants")
    public abstract CommunitySimple communityToCommunitySimple(Community community);

    public abstract List<CommunitySimple> communityToCommunitySimple(List<Community> community);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "classID", source = "user.classId")
    @Mapping(target = "userID", source = "user.userId")
    @Mapping(target = "major", source = "user.subject")
    @Mapping(target = "college", source = "user.college")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "qq", source = "user.qq")
    @Mapping(target = "avatar", source = "user.avatar")
    public abstract CommunityParticipantSimple communityParticipantToCommunityParticipantSimple(CommunityParticipant communityParticipant);

    public abstract Collection<CommunityParticipantSimple> communityParticipantToCommunityParticipantSimple(Collection<CommunityParticipant> communityParticipant);

    public abstract CommunityParticipantResponse communityToCommunityParticipantResponse(Community community);

    public abstract CommunityDetail communityToCommunityDetail(Community community);

    @Mapping(target = "image", source = "cover")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(source = "community.title", target = "communityTitle")
    public abstract CommunityRecordDto communityRecordToCommunityRecordDto(CommunityRecord communityRecord);

    public abstract List<CommunityRecordDto> communityRecordToCommunityRecordDto(List<CommunityRecord> communityRecord);

    @Mapping(source = "communityId", target = "community")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateCommunityRecordFromCommunityRecordRequestBody(CommunityRecordRequestBody communityRecordRequestBody, @MappingTarget CommunityRecord communityRecord);

    @Mapping(source = "participant.user.name",target = "name")
    @Mapping(source = "participant.user.userId",target = "id")
    @Mapping(source = "participant.user.college",target = "college")
    @Mapping(source = "community.title",target = "communityName")
    @Mapping(source = "community.type.name",target = "communityType")
    @Mapping(source = "participant.type",target = "type")
    @Mapping(source = "participant.valid",target = "valid")
    public abstract CommunityParticipantExcel toCommunityParticipantExcel(CommunityParticipant participant,int total,Long attendCount,Community community);

    public CommunityParticipantExcel toCommunityParticipantExcel(CommunityParticipant participant,Community community){
        return toCommunityParticipantExcel(participant,community.getRecords().size(),attendRepository.countByUserAndRecord_Community(participant.getUser(),community),community);
    }

    public  List<CommunityParticipantExcel> toCommunityParticipantExcel(List<CommunityParticipant> participants, Community community){
        ArrayList<CommunityParticipantExcel> participantExcels=new ArrayList<>(participants.size());
        for (CommunityParticipant participant:participants){
            participantExcels.add(toCommunityParticipantExcel(participant,community));
        }
        return participantExcels;
    }
}
