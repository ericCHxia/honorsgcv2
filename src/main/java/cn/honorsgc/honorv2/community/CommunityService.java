package cn.honorsgc.honorv2.community;

import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.repository.CommunityAttendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityService {
    @Autowired
    private CommunityAttendRepository attendRepository;
    Long getParticipantAttendCount(CommunityParticipant participant){
        return attendRepository.countByUserAndRecord_Community(participant.getUser(),participant.getCommunity());
    }
}
