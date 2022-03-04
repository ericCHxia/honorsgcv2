package cn.honorsgc.honorv2.community.repository;

import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommunityRecordRepository extends JpaRepository<CommunityRecord,Integer> , JpaSpecificationExecutor<CommunityRecord> {
    List<CommunityRecord> findAllByCommunity(Community community);
}
