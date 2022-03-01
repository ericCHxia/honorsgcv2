package cn.honorsgc.honorv2.community.repository;

import cn.honorsgc.honorv2.community.entity.CommunityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommunityTypeRepository extends JpaRepository<CommunityType, Integer>, JpaSpecificationExecutor<CommunityType> {
}