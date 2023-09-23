package cn.honorsgc.honorv2.community;

import cn.honorsgc.honorv2.HonorConfigRepository;
import cn.honorsgc.honorv2.community.dto.*;
import cn.honorsgc.honorv2.community.entity.Community;
import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import cn.honorsgc.honorv2.community.entity.CommunityRecord;
import cn.honorsgc.honorv2.community.entity.CommunityType;
import cn.honorsgc.honorv2.community.excel.CommunityParticipantExport;
import cn.honorsgc.honorv2.community.exception.CommunityAccessDenied;
import cn.honorsgc.honorv2.community.exception.CommunityException;
import cn.honorsgc.honorv2.community.exception.CommunityIllegalParameterException;
import cn.honorsgc.honorv2.community.exception.CommunityNotFoundException;
import cn.honorsgc.honorv2.community.mapper.CommunityMapper;
import cn.honorsgc.honorv2.community.repository.CommunityParticipantRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRecordRepository;
import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import cn.honorsgc.honorv2.community.rpc.HduExporterClient;
import cn.honorsgc.honorv2.community.util.CommunityUtil;
import cn.honorsgc.honorv2.core.*;
import cn.honorsgc.honorv2.user.User;
import cn.honorsgc.honorv2.user.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/community")
@Api(tags = "共同管理")
@Slf4j
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
    private HduExporterClient exporterClient;
    @Autowired
    private CommunityUtil communityUtil;
    @Autowired
    private HonorConfigRepository honorConfigRepository;

    @PostMapping({"", "/"})
    @ApiOperation(value = "新建共同体")
    public CommunitySaveResponseBody postCommunity(@ApiIgnore Authentication authentication,
                                                   @Validated({CreateWish.class}) @RequestBody CommunityRequestBody requestBody,
                                                   @ApiIgnore Errors errors) throws CommunityIllegalParameterException {
        User user = checkUser(authentication, errors);
        Community community = new Community();
        community.setUser(user);
        community.setCreateDate(new Date());
        community.setState(0);
        community.setEnrolling(true);
        // 如果不是管理员的话，就将文章的状态设为null，在转换器中能够自动转换为 `CommunityState.notApproved`
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            requestBody.setState(null);
        }

        communityMapper.updateCommunityFromCommunityRequestBody(requestBody, community);
        community.setId(null);
        community = repository.save(community);

        return communityMapper.communityToCommunitySaveResponseBody(community);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改共同体")
    public CommunitySaveResponseBody updateCommunity(@ApiIgnore Authentication authentication,
                                                     @ApiParam(value = "编号") @PathVariable Long id,
                                                     @Validated({UpdateWish.class}) @RequestBody CommunityRequestBody requestBody,
                                                     @ApiIgnore Errors errors) throws CommunityIllegalParameterException {
        User user = checkUser(authentication, errors);
        Community community = repository.getById(id);
        if (!community.getUser().equals(user) && !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new CommunityAccessDenied();
        }

        // 如果不是管理员的话，就将文章的状态设为null，就不会改变当前状态
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            requestBody.setState(null);
        }

        communityMapper.updateCommunityFromCommunityRequestBody(requestBody, community);
        community = repository.save(community);

        return communityMapper.communityToCommunitySaveResponseBody(community);
    }

    private User checkUser(@ApiIgnore Authentication authentication, @ApiIgnore Errors errors) throws CommunityIllegalParameterException {
        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().get(0);
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                throw new CommunityIllegalParameterException(fieldError.getField() + fieldError.getDefaultMessage());
            }
            throw new CommunityIllegalParameterException(objectError.getDefaultMessage());
        }
        return (User) authentication.getPrincipal();
    }

    @GetMapping({"", "/"})
    public Page<CommunitySimple> getCommunities(@ApiIgnore Authentication authentication,
                                                @ApiParam(value = "页号") @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
                                                @ApiParam(value = "类型") @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
                                                @ApiParam(value = "用户编号") @RequestParam(value = "user", required = false, defaultValue = "-1") Long userId,
                                                @ApiParam(value = "状态", allowableValues = "0,1,2") @RequestParam(required = false, defaultValue = "-1") Integer state,
                                                @ApiParam(value = "搜索文本") @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                @ApiParam(value = "使用管理员权限") @RequestParam(required = false, defaultValue = "false") Boolean admin,
                                                @ApiParam(value = "参与用户") @RequestParam(value = "participant", required = false, defaultValue = "-1") Long participantId,
                                                @ApiParam(value = "页面大小") @RequestParam(value = "page_size", required = false, defaultValue = "25") Integer pageSize,
                                                @RequestParam(value = "mentor", required = false, defaultValue = "-1") Long mentorId,
                                                @RequestParam(value = "semester", required = false, defaultValue = "-1") Integer semester) throws CommunityException {
        if (pageSize>50)
        {
            throw new CommunityIllegalParameterException("page_size 应小于等于50");
        }

        //TODO 实现参与用户 和 管理用户的筛选
        User user = (User) authentication.getPrincipal();
        admin = user.getAuthorities().contains(GlobalAuthority.ADMIN) && admin;
        if (admin && state != -1) {
            if (state < 0 || state >= 3) {
                throw new CommunityIllegalParameterException("state参数错误");
            }
        }

        Semester currentSemester = Semester.valuesOf(honorConfigRepository.findAll().get(0).getSemester());

        boolean finalAdmin = admin;
        Specification<Community> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type >= 0) {
                list.add(cb.equal(root.get("type").get("id"), type));
            }
            if (finalAdmin && state != -1) {
                list.add(cb.equal(root.get("state"), state));
            } else if (!finalAdmin) {
                list.add(cb.or(cb.equal(root.get("state"), 1), cb.equal(root.get("user").get("id"), user.getId())));
                list.add(cb.between(root.get("createDate"), currentSemester.getBegin(), currentSemester.getEnd()));
            }
            if (finalAdmin && semester > 0) {
                Semester tmpSemester = Semester.valuesOf(semester);
                list.add(cb.between(root.get("createDate"), tmpSemester.getBegin(), tmpSemester.getEnd()));
            }
            if (userId != -1) {
                list.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (mentorId != -1) {
                list.add(root.join("mentors").get("user").get("id").in(mentorId));
            }
            if (participantId != -1) {
                list.add(root.join("participants").get("user").get("id").in(participantId));
            }
            if (!search.equals("")) list.add(cb.like(root.get("title"), "%" + search + "%"));
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Community> pages = repository.findAll(specification, pageable);

        return new PageImpl<>(communityMapper.communityToCommunitySimple(pages.getContent()), pageable, pages.getTotalElements());
    }

    @GetMapping("/{id}")
    public CommunityDetail getCommunity(@ApiIgnore Authentication authentication,
                                        @ApiParam(value = "编号") @PathVariable Long id) throws CommunityException {
        Optional<Community> optionalCommunity = repository.findById(id);
        if (optionalCommunity.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        Community community = optionalCommunity.get();
        if (community.getState() != CommunityState.visible && !community.getUser().equals(authentication.getPrincipal())&& !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityDetail(community);
    }

    @DeleteMapping("/{id}")
    public GlobalResponseEntity<String> delCommunity(@ApiIgnore Authentication authentication,
                                                     @ApiParam(value = "编号") @PathVariable Long id) throws CommunityException {
        Community community = communityUtil.communityIsExist(id, authentication);
        repository.delete(community);
        return new GlobalResponseEntity<>("");
    }

    @GetMapping("/type")
    @ApiOperation("获取类型")
    public List<CommunityType> getType() {
        return typeRepository.findAll();
    }

    @PostMapping("/type")
    @ApiOperation("添加类型")
    @Secured({"ROLE_ADMIN"})
    public CommunityType createType(@ApiParam(value = "共同体类型名") @RequestParam(value = "typeName") String typeName) throws CommunityException {
        if (typeRepository.existsByName(typeName)) {
            throw new CommunityIllegalParameterException("共同体类型 " + typeName + " 已存在");
        }
        CommunityType communityType = new CommunityType();
        communityType.setName(typeName);
        return typeRepository.save(communityType);
    }

    @DeleteMapping("/type/{id}")
    @Secured({"ROLE_ADMIN"})
    public GlobalResponseEntity<String> deleteType(@PathVariable Integer id) throws CommunityException {
        Optional<CommunityType> optionalCommunityType = typeRepository.findById(id);
        if (optionalCommunityType.isEmpty()) {
            throw new CommunityIllegalParameterException("type 不存在");
        }
        if (optionalCommunityType.get().getCount() > 0) {
            throw new CommunityIllegalParameterException("该类型共同体数不为0");
        }
        typeRepository.delete(optionalCommunityType.get());
        return new GlobalResponseEntity<>("ok");
    }

    @GetMapping("/participant/{id}")
    @ApiOperation("获取参与者")
    public CommunityParticipantResponse getParticipant(@PathVariable Long id) throws CommunityException {
        Optional<Community> community = repository.findById(id);
        if (community.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityParticipantResponse(community.get());
    }

    @PostMapping("/participant/{id}/export")
    @ApiOperation("导出参与者")
    public void exportParticipant(@PathVariable Long id, HttpServletResponse response, @RequestParam(value = "ids", required = false) List<Long> userIds) throws CommunityException, IOException {
        Optional<Community> optionalCommunity = repository.findById(id);
        if (optionalCommunity.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        Community                  community       = optionalCommunity.get();
        List<CommunityParticipant> participants    = new ArrayList<>(community.getParticipants());
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";

        participants.addAll(community.getMentors());
        if (userIds != null && userIds.size() > 0) {
            participants.removeIf(p -> !userIds.contains(p.getUser().getId()));
        }
        response.setHeader("Content-Disposition", headerValue);

        List<CommunityRecord> records = communityRecordRepository.findAllByCommunity(community);
        Map<CommunityParticipant, Integer> participantCountMap = communityUtil.getParticipantRecord(records, community);
        CommunityParticipantExport.valueOf(participantCountMap, records.size()).export(response);
    }

    @PostMapping("/join")
    @ApiOperation("添加参与者")
    public GlobalResponseEntity<String> joinCommunity(@ApiIgnore Authentication authentication,
                                                      @RequestParam(value = "id") Long id,
                                                      @RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
                                                      @RequestParam(value = "delete", required = false, defaultValue = "1") Boolean delete) throws CommunityException {

        //检查共同体的有效性
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(id, authentication);
        if (!delete) {
            if (!community.getEnrolling()) {
                throw new CommunityIllegalParameterException("报名停止");
            }
            //检查是否能够审批 需要审批register是1
            //检查共同体人数限制
            //若不需要审批，则判断参与人数
            else if (type == 0 && community.getLimit() <= community.getParticipantsCount() && community.getLimit() > 0 && community.getRegistrationType() == 0) {
                throw new CommunityIllegalParameterException("参与人数超过限制");
            }
            if (type == 1 && community.getMentors().size() >= 2) {
                throw new CommunityIllegalParameterException("指导人数超过限制");
            }
            if (type != 0 && type != 1) {
                throw new CommunityIllegalParameterException("指导类型错误");
            }
            //检查参与人
            User    user          = (User) authentication.getPrincipal();
            boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.getUser().equals(user));
            boolean isMentor      = community.getMentors().stream().anyMatch(x -> x.getUser().equals(user));
            if (isParticipant || isMentor) {
                if (community.getRegistrationType() == 0) {
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
        } else {
            User    user          = (User) authentication.getPrincipal();
            boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.getUser().equals(user));
            boolean isMentor      = community.getMentors().stream().anyMatch(x -> x.getUser().equals(user));
            if (!isMentor && !isParticipant) {
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
        Community community = communityUtil.communityIsExist(communityId, authentication);

        //检查创建者
        User auth = (User) authentication.getPrincipal();
        if (!auth.equals(community.getUser())&&!authentication.getAuthorities().contains(GlobalAuthority.ADMIN))
            throw new CommunityIllegalParameterException("您不是创建者");

        Specification<CommunityParticipant> specification = (root, query, cb) -> cb.and(cb.equal(root.get("communityId"), community.getId()), root.get("user").get("id").in(userIds));
        List<CommunityParticipant>          cpList        = communityParticipantRepository.findAll(specification);

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
        Community community = communityUtil.communityIsExist(communityId, authentication);
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
        Community community = communityUtil.communityIsExist(communityId, authentication);

        //判断当前登录人是否为参加者
        User    auth          = (User) authentication.getPrincipal();
        boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.getUser().equals(auth));
        boolean isMentor      = community.getMentors().stream().anyMatch(x -> x.getUser().equals(auth));

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

    @GetMapping("/rec/{id}")
    @ApiOperation("查看记录")
    public List<CommunityRecordDto> getRecord(@ApiParam(value = "共同体编号") @PathVariable(value = "id") Long communityId,
                                              @ApiIgnore Authentication authentication) throws CommunityException {
        //判断共同体是否存在
        Community community = communityUtil.communityIsExist(communityId, authentication);

        return communityMapper.communityRecordToCommunityRecordDto(communityRecordRepository.findAllByCommunity(community));
    }

    @GetMapping("/rec")
    @ApiOperation("搜索记录")
    @Secured("ROLE_ADMIN")
    public Page<CommunityRecordDto> getRecords(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        size = size > 100 ? 100 : size;
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommunityRecord> communityRecordPage = communityRecordRepository.findAll(pageable);
        return communityRecordPage.map(communityRecord -> communityMapper.communityRecordToCommunityRecordDto(communityRecord));
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
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
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

    @GetMapping("/state")
    public Object getCommunityState() {
        Semester                 semester      = Semester.valuesOf(honorConfigRepository.findAll().get(0).getSemester());
        Specification<Community> specification = (root, query, cb) -> cb.between(root.get("createDate"), semester.getBegin(), semester.getEnd());
        Long                     total         = repository.count(specification);
        Map<String, Long>        map           = new HashMap<>();
        map.put("total", total);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            Specification<Community> specificationSate = (root, query, cb) -> {
                Predicate predicate = cb.between(root.get("createDate"), semester.getBegin(), semester.getEnd());
                return cb.and(predicate, cb.equal(root.get("state"), finalI));
            };
            map.put(String.valueOf(i), repository.count(specificationSate));
        }
        return map;
    }

    @PostMapping("/export")
    public GlobalResponseEntity<String> exportCommunity(@RequestParam(value = "ids", required = false) List<Long> communityIds) {
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage(exporterClient.ExportAttend(communityIds));
        return responseEntity;
    }
}
