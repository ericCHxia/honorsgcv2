package cn.honorsgc.honorv2.community.convert;

import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityType;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommunityConvert {
    @Autowired
    private CommunityTypeRepository typeRepository;
    @Autowired
    private CommunityRepository repository;

    public CommunityType ToCommunityType(Integer value){
        Optional<CommunityType> optionalCommunityType = typeRepository.findById(value);
        return optionalCommunityType.orElse(null);
    }

    public Community ToCommunity(Long value){
        Optional<Community> optionalCommunity = repository.findById(value);
        return optionalCommunity.orElse(null);
    }
}
