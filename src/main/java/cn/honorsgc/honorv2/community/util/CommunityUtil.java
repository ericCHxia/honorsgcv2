package cn.honorsgc.honorv2.community.util;

import cn.honorsgc.honorv2.community.CommunityState;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.repository.CommunityParticipantRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRecordRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import cn.honorsgc.honorv2.core.GlobalAuthority;
import cn.honorsgc.honorv2.user.User;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommunityUtil {
    @Autowired
    public CommunityRepository repository;
    @Autowired
    private CommunityRecordRepository recordRepository;
    @Autowired
    private CommunityParticipantRepository participantRepository;

    public Community communityIsExist(Long communityId, Authentication authentication) throws CommunityException {
        Optional<Community> optionalCommunity = repository.findById(communityId);
        if (optionalCommunity.isEmpty()) {
            throw new CommunityIllegalParameterException("共同体不存在");
        }
        Community community = optionalCommunity.get();
        if (!authentication.getPrincipal().equals(community.getUser()) && community.getState() != CommunityState.visible && !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
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
            year = String.valueOf((Integer.parseInt(year) - 1));
        } else term = "上";
        System.out.println();
        return year + "学年" + term + "学期";
    }

    //TODO: 优化统计出勤
    public Map<CommunityParticipant, Integer> getParticipantRecord(List<CommunityRecord> records, Community community) {
        MultiSet<User> multiSet = new HashMultiSet<>();
        records.forEach(record -> multiSet.addAll(record.getAttendant()));
        return multiSet.uniqueSet().stream().collect(HashMap::new, (m, v) ->
                m.put(participantRepository.findCommunityParticipantByUserAndCommunityId(v, community.getId()), multiSet.getCount(v)), Map::putAll);
    }
}
