package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.core.CreateWish;
import cn.honorsgc.honorv2.core.UpdateWish;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class CommunityRequestBodyGroupSequenceProvider implements DefaultGroupSequenceProvider<CommunityRequestBody> {
    @Override
    public List<Class<?>> getValidationGroups(CommunityRequestBody requestBody) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(CommunityRequestBody.class);
        if (requestBody!=null){
            if (requestBody.getUpdate()){
                defaultGroupSequence.add(UpdateWish.class);
            }else {
                defaultGroupSequence.add(CreateWish.class);
            }
        }
        return defaultGroupSequence;
    }
}
