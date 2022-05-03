package cn.honorsgc.honorv2.community.repository;

import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityAttend;
import cn.honorsgc.honorv2.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommunityAttendRepository extends JpaRepository<CommunityAttend, Long>, JpaSpecificationExecutor<CommunityAttend> {
    Long countByUserAndRecord_Community(User user, Community community);
}