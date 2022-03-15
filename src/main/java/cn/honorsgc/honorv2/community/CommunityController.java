package cn.honorsgc.honorv2.community;

import cn.honorsgc.honorv2.community.dto.*;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import cn.honorsgc.honorv2.community.entity.CommunityType;
import cn.honorsgc.honorv2.community.exception.CommunityAccessDenied;
import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.exception.CommunityNotFoundException;
import cn.honorsgc.honorv2.community.mapper.CommunityMapper;
import cn.honorsgc.honorv2.community.repository.CommunityParticipantRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRecordRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import cn.honorsgc.honorv2.community.util.CommunityUtil;
import cn.honorsgc.honorv2.core.GlobalAuthority;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private CommunityParticipantRepository communityParticipantRepository;
    @Autowired
    private CommunityRecordRepository communityRecordRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CommunityUtil communityUtil;

    private final Logger logger = LoggerFactory.getLogger(CommunityController.class);

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

    @GetMapping({"", "/"})
    public Page<CommunitySimple> getCommunities(@ApiIgnore Authentication authentication,
                                                @ApiParam(value = "页号") @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
                                                @ApiParam(value = "类型") @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
                                                @ApiParam(value = "用户编号") @RequestParam(value = "user", required = false, defaultValue = "-1") Long userId,
                                                @ApiParam(value = "状态", allowableValues = "0,1,2") @RequestParam(required = false) Integer state,
                                                @ApiParam(value = "搜索文本") @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                @ApiParam(value = "使用管理员权限") @RequestParam(required = false, defaultValue = "false") Boolean admin,
                                                @ApiParam(value = "参与用户") @RequestParam(value = "participant", required = false, defaultValue = "-1") Long participantId,
                                                @RequestParam(value = "mentor", required = false, defaultValue = "-1") Long mentorId
    ) throws CommunityException {
        //TODO 实现参与用户 和 管理用户的筛选
        User user = (User) authentication.getPrincipal();

        if (admin && state != null && authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            if (state < 0 || state > 3) {
                throw new CommunityIllegalParameterException("state参数错误");
            }
        }

        Specification<Community> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type >= 0) {
                list.add(cb.equal(root.get("type").get("id"), type));
            }
            if (admin && state != null && authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
                list.add(cb.equal(root.get("state"), state));
            } else {
                list.add(cb.or(cb.equal(root.get("state"), 1), cb.equal(root.get("user").get("id"), user.getId())));
            }

            if (!search.equals("")) list.add(cb.like(root.get("title"), "%" + search + "%"));
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");

        Pageable pageable = PageRequest.of(pageNumber, 25, sort);

        Page<Community> pages = repository.findAll(specification, pageable);

        return new PageImpl<>(communityMapper.communityToCommunitySimple(pages.getContent()), pageable, pages.getTotalElements());
    }

    @GetMapping("/{id}")
    public CommunityDetail getCommunity(@ApiIgnore Authentication authentication,
                                        @ApiParam(value = "编号") @PathVariable Long id) throws CommunityException {
        Optional<Community> optionalCommunity = repository.findById(id);
        logger.info("012");
        if (optionalCommunity.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        Community community = optionalCommunity.get();
        logger.info("123");
        if (community.getState() != CommunityState.visible && !(community.getUser().equals(authentication.getPrincipal()) || authentication.getAuthorities().contains(GlobalAuthority.ADMIN))) {
            throw new CommunityNotFoundException();
        }
        logger.info("456");
        return communityMapper.communityToCommunityDetail(community);
    }

    @GetMapping("/type")
    @ApiOperation("获取类型")
    public List<CommunityType> getType() {
        return typeRepository.findAll();
    }

    @PostMapping("/type")
    @ApiOperation("添加类型")
    public CommunityType createType(@ApiIgnore Authentication authentication,
                                    @ApiParam(value = "共同体类型名") @RequestParam(value = "typeName") String typeName) throws CommunityException {
        User auth = (User) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new CommunityIllegalParameterException("您不是管理员");
        }
        CommunityType communityType = new CommunityType();
        communityType.setName(typeName);
        return typeRepository.save(communityType);
    }

    @GetMapping("/participant")
    @ApiOperation("获取参与者")
    public CommunityParticipantResponse getParticipant(@RequestParam(value = "id") Long id) throws CommunityException {
        Optional<Community> community = repository.findById(id);
        if (community.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityParticipantResponse(community.get());
    }

    @GetMapping("/join")
    @ApiOperation("添加参与者")
    public GlobalResponseEntity<String> joinCommunity(@ApiIgnore Authentication authentication,
                                                      @RequestParam(value = "id") Long id,
                                                      @RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
                                                      @RequestParam(value = "delete", required = false, defaultValue = "1") Boolean delete) throws CommunityException {

        //检查共同体的有效性
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(id);
        if(!delete){
            if (!community.getEnrolling()) {
                throw new CommunityIllegalParameterException("报名停止");
            }
            //检查是否能够审批 需要审批register是1
            //检查共同体人数限制
            //若不需要审批，则判断参与人数
            else if (type == 0 && (community.getLimit() <= 0 || community.getLimit() <= community.getParticipantsCount()) && community.getRegistrationType() != 0) {
                throw new CommunityIllegalParameterException("参与人数超过限制");
            }
            if (type == 1 && community.getMentors().size() >= 2) {
                throw new CommunityIllegalParameterException("指导人数超过限制");
            }
            if (type != 0 && type != 1) {
                throw new CommunityIllegalParameterException("指导类型错误");
            }
            //检查参与人
            User user = (User) authentication.getPrincipal();
            boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.equals(user));
            boolean isMentor = community.getMentors().stream().anyMatch(x -> x.equals(user));
            if (isParticipant || isMentor) {
                if (community.getRegistrationType() == 1) {
                    throw new CommunityIllegalParameterException("您已参加");
                } else {
                    CommunityParticipant cp = communityParticipantRepository.findCommunityParticipantByUserAndCommunityId(user, id);
                    if (cp.getValid()) {
                        throw new CommunityIllegalParameterException("您已参加");
                    } else throw new CommunityIllegalParameterException("正在等待审核");
                }
            }

            CommunityParticipant participant = new CommunityParticipant();
            participant.setUser(user);
            participant.setType(type);
            participant.setCommunityId(id);
            participant.setValid(community.getRegistrationType() == 0);
            communityParticipantRepository.save(participant);
        }

        else {
            User user = (User) authentication.getPrincipal();
            boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.equals(user));
            boolean isMentor = community.getMentors().stream().anyMatch(x -> x.equals(user));
            if(!isMentor&&!isParticipant){
                throw new CommunityIllegalParameterException("您未参加该共同体");
            }
            CommunityParticipant cp = communityParticipantRepository.findCommunityParticipantByUserAndCommunityId(user, id);
            cp.setCommunityId(null);
            communityParticipantRepository.save(cp);
        }



        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("success");
        return responseEntity;
    }

    @PostMapping("approve")
    @ApiOperation("审核")
    public GlobalResponseEntity<String> approveJoin(@ApiIgnore Authentication authentication,
                                                    @RequestParam(value = "communityId") Long communityId,
                                                    @RequestParam(value = "type", required = false, defaultValue = "1") Boolean type,
                                                    @RequestParam(value = "userId", required = false, defaultValue = "-1") List<Long> userIds) throws CommunityException {
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(communityId);

        //检查创建者
        User auth = (User) authentication.getPrincipal();
        if (!auth.equals(community.getUser()))
            throw new CommunityIllegalParameterException("您不是创建者");

        Specification<CommunityParticipant> specification = (root, query, cb) -> cb.and(cb.equal(root.get("communityId"), community.getId()), root.get("user").get("id").in(userIds));
        List<CommunityParticipant> cpList = communityParticipantRepository.findAll(specification);

        for (CommunityParticipant participant : cpList) {
            participant.setValid(type);
        }
        //保存
        communityParticipantRepository.saveAll(cpList);
        return new GlobalResponseEntity<>(0, "审核成功");
    }

    @DeleteMapping("/participant")
    @ApiOperation("删除参加者")
    public GlobalResponseEntity<String> delParticipant(@ApiIgnore Authentication authentication,
                                                       @RequestParam(value = "communityId") Long communityId,
                                                       @ApiParam(value = "用户的编号") @RequestParam(value = "ids", required = false, defaultValue = "-1") Set<Long> ids) throws CommunityException {

        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(communityId);
        //检查删除权限
        User auth = (User) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN) && !auth.equals(community.getUser())) {
            throw new CommunityIllegalParameterException("您无权删除");
        }

        community.removeParticipant(ids);
        repository.save(community);

        return new GlobalResponseEntity<>(0, "删除成功");
    }

    @PostMapping("/rec")
    @ApiOperation("添加记录")
    public CommunityRecord createRecord(@RequestBody CommunityRecordRequestBody recordRequestBody,
                                        @ApiIgnore Authentication authentication) throws CommunityException {
        Long communityId = recordRequestBody.getCommunityId();
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(communityId);

        //判断当前登录人是否为参加者
        User auth = (User) authentication.getPrincipal();
        boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.equals(auth));
        boolean isMentor = community.getMentors().stream().anyMatch(x -> x.equals(auth));

        if (!isMentor && !isParticipant) {
            throw new CommunityIllegalParameterException("您无权添加");
        }


        CommunityRecord communityRecord = new CommunityRecord();
        communityMapper.updateCommunityRecordFromCommunityRecordRequestBody(recordRequestBody, communityRecord);
        communityRecord.setUser(auth);
        communityRecord.setCreateTime(new Date());

        //return communityRecord;
        return communityRecordRepository.save(communityRecord);
    }

    @GetMapping("/rec")
    @ApiOperation("查看记录")
    public List<CommunityRecord> getRecord(@ApiParam(value = "共同体编号") @RequestParam(value = "communityId") Long communityId,
                                           @ApiIgnore Authentication authentication) throws CommunityException, JsonProcessingException {
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(communityId);
        //判断当前登录人是否为参加者或管理员
        User auth = (User) authentication.getPrincipal();
        boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.equals(auth));
        boolean isMentor = community.getMentors().stream().anyMatch(x -> x.equals(auth));
        System.out.println();
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN) && !isParticipant && !isMentor) {
            throw new CommunityIllegalParameterException("您无权查看");
        }
        return communityRecordRepository.findAllByCommunity(community);
    }

    //TODO: 共同体的创建者可以删除本共同体内的全部记录
    @DeleteMapping("/record")
    @ApiOperation("删除记录")
    public GlobalResponseEntity<String> deleteRecord(@ApiParam(value = "记录编号") @RequestParam List<Integer> ids,
                                                     @ApiIgnore Authentication authentication) throws CommunityException {

        List<CommunityRecord> communityRecordList = communityRecordRepository.findAllById(ids);
        if (communityRecordList.isEmpty()) {
            throw new CommunityIllegalParameterException("未找到记录");
        }

        User auth = (User) authentication.getPrincipal();
        //管理员直接删
        if(!authentication.getAuthorities().contains(GlobalAuthority.ADMIN) )
        {
            //若不是管理员过滤掉不是本人发布的信息
            communityRecordList = communityRecordList.stream().filter(a -> Objects.equals(a.getUser().getId(), auth.getId())).collect(Collectors.toList());
            if (communityRecordList.isEmpty()) {
                throw new CommunityIllegalParameterException("没有您发布的记录");
            }
        }

        communityRecordRepository.deleteAll(communityRecordList);
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("delete successfully");
        return responseEntity;
    }
}
