package cn.honorsgc.honorv2.community.repository;

import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommunityParticipantRepository extends JpaRepository<CommunityParticipant, Integer>, JpaSpecificationExecutor<CommunityParticipant> {
    CommunityParticipant findCommunityParticipantByUserAndCommunityId(User user,Long id);
}
