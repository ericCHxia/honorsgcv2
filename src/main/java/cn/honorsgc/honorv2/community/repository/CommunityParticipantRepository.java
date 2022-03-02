package cn.honorsgc.honorv2.community.repository;

import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommunityParticipantRepository extends JpaRepository<CommunityParticipant, Integer>, JpaSpecificationExecutor<CommunityParticipant> {
    CommunityParticipant findCommunityParticipantByUsersAndCommunityId(User user,Long communityId);
}
