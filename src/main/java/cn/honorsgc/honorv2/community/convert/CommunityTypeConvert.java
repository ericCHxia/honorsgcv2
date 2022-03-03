package cn.honorsgc.honorv2.community.convert;

import cn.honorsgc.honorv2.community.entity.CommunityType;
import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommunityTypeConvert {
    @Autowired
    private CommunityTypeRepository typeRepository;

    public CommunityType ToCommunityType(Integer value){
        Optional<CommunityType> optionalCommunityType = typeRepository.findById(value);
        return optionalCommunityType.orElse(null);
    }
}
