package cn.honorsgc.honorv2.community;

import cn.honorsgc.honorv2.community.dto.*;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityType;
import cn.honorsgc.honorv2.community.exception.CommunityAccessDenied;
import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.exception.CommunityNotFoundException;
import cn.honorsgc.honorv2.community.mapper.CommunityMapper;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import cn.honorsgc.honorv2.core.GlobalAuthority;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/community")
@Api(tags = "共同管理")
public class CommunityController {
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private CommunityRepository repository;
    @Autowired
    private CommunityTypeRepository typeRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping({"", "/"})
    @ApiOperation(value = "新建或者修改共同体")
    public CommunitySaveResponseBody postCommunity(@ApiIgnore Authentication authentication,
                                                   @Valid @RequestBody CommunityRequestBody requestBody,
                                                   @ApiIgnore Errors errors) throws CommunityIllegalParameterException {
        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().get(0);
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                throw new CommunityIllegalParameterException(fieldError.getField() + fieldError.getDefaultMessage());
            }
            throw new CommunityIllegalParameterException(objectError.getDefaultMessage());
        }

        User user = (User) authentication.getPrincipal();
        Community community;
        if (requestBody.getUpdate()) {
            community = repository.getById(requestBody.getId());
            if (!community.getUser().equals(user) && !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
                throw new CommunityAccessDenied();
            }
        } else {
            community = new Community();
            community.setUser(user);
            community.setCreateDate(new Date());
        }

        // 如果不是管理员的话，就将文章的状态设为null，在转换器中能够自动转换为 `CommunityState.notApproved`
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            requestBody.setState(null);
        }

        communityMapper.updateCommunityFromCommunityRequestBody(requestBody, community);
        community = repository.save(community);

        return communityMapper.communityToCommunitySaveResponseBody(community);
    }

    @GetMapping({"","/"})
    public Page<CommunitySimple> getCommunities(@ApiIgnore Authentication authentication,
                                              @ApiParam(value = "页号") @RequestParam(value = "page",required = false,defaultValue = "0") Integer pageNumber,
                                              @ApiParam(value = "类型") @RequestParam(value = "type",required = false,defaultValue = "-1") Integer type,
                                              @ApiParam(value = "用户编号") @RequestParam(value = "user",required = false,defaultValue = "-1") Long userId,
                                              @ApiParam(value = "状态",allowableValues = "0,1,2") @RequestParam(required = false) Integer state,
                                              @ApiParam(value = "搜索文本") @RequestParam(value = "search",required = false,defaultValue = "")String search,
                                              @ApiParam(value = "使用管理员权限") @RequestParam(required = false,defaultValue = "false")Boolean admin,
                                              @ApiParam(value = "参与用户") @RequestParam(value = "participant",required = false,defaultValue = "-1")Long participantId,
                                              @RequestParam(value = "mentor",required = false,defaultValue = "-1")Long mentorId
                                        )throws CommunityException {
        //TODO 实现参与用户 和 管理用户的筛选
        User user = (User) authentication.getPrincipal();

        if (admin&&state!=null&&authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            if (state < 0 || state > 3) {
                throw new CommunityIllegalParameterException("state参数错误");
            }
        }

        Specification<Community> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type>=0){
                list.add(cb.equal(root.get("type").get("id"),type));
            }
            if (admin&&state!=null&&authentication.getAuthorities().contains(GlobalAuthority.ADMIN)){
                list.add(cb.equal(root.get("state"),state));
            }else {
                list.add(cb.or(cb.equal(root.get("state"),1),cb.equal(root.get("user").get("id"),user.getId())));
            }

            if (!search.equals(""))list.add(cb.like(root.get("title"),"%"+search+"%"));
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        };

        Sort sort = Sort.by(Sort.Direction.DESC,"createDate");

        Pageable pageable = PageRequest.of(pageNumber,25,sort);

        Page<Community> pages= repository.findAll(specification,pageable);

        return new PageImpl<>(communityMapper.communityToCommunitySimple(pages.getContent()), pageable, pages.getTotalElements());
    }

    @GetMapping("/{id}")
    public CommunityDetail getCommunity(@ApiIgnore Authentication authentication,
                                        @ApiParam(value = "编号") @PathVariable Long id)throws CommunityException{
        Optional<Community> optionalCommunity = repository.findById(id);
        if(optionalCommunity.isEmpty()){
            throw new CommunityNotFoundException();
        }
        Community community = optionalCommunity.get();
        if (community.getState()!=CommunityState.visible&&!(community.getUser().equals(authentication.getPrincipal())||authentication.getAuthorities().contains(GlobalAuthority.ADMIN))){
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityDetail(community);
    }

    @GetMapping("/type")
    public List<CommunityType> getType(){
        return typeRepository.findAll();
    }

    @GetMapping("/participant")
    @ApiOperation("获取参与者")
    public CommunityParticipantResponse getParticipant(@RequestParam(value = "id")Long id) throws CommunityException{
        Optional<Community> community = repository.findById(id);
        if (community.isEmpty()){
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityParticipantResponse(community.get());
    }

    @GetMapping("/join")
    @ApiOperation("添加参与者")
    public String joinCommunity(@ApiIgnore Authentication authentication,
                                @RequestParam(value = "id") Long id,
                                @RequestParam(value = "type",required = false,defaultValue = "0") Integer type,
                                @RequestParam(value = "delete",required = false,defaultValue = "1") Boolean delete,
                                @RequestParam(value = "userId",required = false,defaultValue = "-1") Long userId)throws CommunityException{

        //检查共同体的有效性
        Optional<Community> optionalCommunity = repository.findById(id);
        if (optionalCommunity.isEmpty()){
            throw new CommunityIllegalParameterException("共同体不存在");
        }

        Community community = optionalCommunity.get();
        if (community.getState()==CommunityState.notApproved){
            throw new CommunityIllegalParameterException("共同体不存在");
        }

        if (!community.getEnrolling()){
            throw new CommunityIllegalParameterException("报名停止");
        }

        //检查共同体人数限制
        if (type==0&&(community.getLimit()<=0||community.getLimit()<=community.getParticipantsCount())){
            throw new CommunityIllegalParameterException("参与人数超过限制");
        }
        if (type==1&&community.getMentors().size()>=2){
            throw new CommunityIllegalParameterException("指导人数超过限制");
        }
        if (type!=0&&type!=1){
            throw new CommunityIllegalParameterException("指导类型错误");
        }

        //检查参与人
        User user;
        if (userId>0){
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()){
                throw new CommunityIllegalParameterException("用户不存在");
            }
            user = optionalUser.get();
        }else {
            user = (User) authentication.getPrincipal();
        }

        CommunityParticipant participant = new CommunityParticipant();
        participant.setCommunityId(community.getId());
        participant.setType(type);
        participant.setUsers(user);

        if (community.getMentors().contains(participant)||community.getParticipants().contains(participant)){
            throw new CommunityIllegalParameterException("您已参与");
        }

        if (participant.getType()==1){
            community.getMentors().add(participant);
        }else {
            community.getParticipants().add(participant);
        }
        repository.save(community);

        return "参加成功";
    }
}
