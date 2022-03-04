package cn.honorsgc.honorv2.community.util;

import cn.honorsgc.honorv2.community.CommunityState;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CommunityUtil {
    @Autowired
    public CommunityRepository repository ;

    public Boolean CommunityIsExist(Long communityId) {
        Optional<Community> optionalCommunity = repository.findById(communityId);
        if (optionalCommunity.isEmpty()) {
            return false;
        }
        Community community = optionalCommunity.get();
        if (community.getState() == CommunityState.notApproved) {
            return false;
        }
        return true;
    }
}
