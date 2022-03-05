package cn.honorsgc.honorv2.community.util;

import cn.honorsgc.honorv2.community.CommunityState;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class CommunityUtil {
    @Autowired
    public CommunityRepository repository;

    public Community CommunityIsExist(Long communityId) throws CommunityException {
        Optional<Community> optionalCommunity = repository.findById(communityId);
        if (optionalCommunity.isEmpty()) {
            throw new CommunityIllegalParameterException("共同体不存在");
        }
        Community community = optionalCommunity.get();
        if (community.getState() == CommunityState.notApproved) {
            throw new CommunityIllegalParameterException("共同体不存在");
        }
        return community;
    }

    public String getTerm(Date date) {
        //从形如xxxx-xx-xx 的日期中获取年月
        String year = date.toString().split("-")[0];
        String month = date.toString().split("-")[1];
        String term;
        if (Integer.parseInt(month) < 9 && Integer.parseInt(month) >= 2) {
            term = "下";
            year= String.valueOf((Integer.parseInt(year)-1));
        } else term = "上";
        System.out.println();
        return year + "学年" + term + "学期";


    }
}
