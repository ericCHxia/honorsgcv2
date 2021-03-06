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
import cn.honorsgc.honorv2.community.util.CommunityUtil;
import cn.honorsgc.honorv2.core.*;
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
@Api(tags = "????????????")
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
    @Autowired
    private HonorConfigRepository honorConfigRepository;

    private final Logger logger = LoggerFactory.getLogger(CommunityController.class);

    @PostMapping({"", "/"})
    @ApiOperation(value = "???????????????")
    public CommunitySaveResponseBody postCommunity(@ApiIgnore Authentication authentication,
                                                   @Validated({CreateWish.class}) @RequestBody CommunityRequestBody requestBody,
                                                   @ApiIgnore Errors errors) throws CommunityIllegalParameterException {
        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().get(0);
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                throw new CommunityIllegalParameterException(fieldError.getField() + fieldError.getDefaultMessage());
            }
            throw new CommunityIllegalParameterException(objectError.getDefaultMessage());
        }

        User      user      = (User) authentication.getPrincipal();
        Community community = new Community();
        community.setUser(user);
        community.setCreateDate(new Date());
        community.setState(0);
        community.setEnrolling(true);
        // ?????????????????????????????????????????????????????????null??????????????????????????????????????? `CommunityState.notApproved`
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            requestBody.setState(null);
        }

        communityMapper.updateCommunityFromCommunityRequestBody(requestBody, community);
        community.setId(null);
        community = repository.save(community);

        return communityMapper.communityToCommunitySaveResponseBody(community);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "???????????????")
    public CommunitySaveResponseBody updateCommunity(@ApiIgnore Authentication authentication,
                                                     @ApiParam(value = "??????") @PathVariable Long id,
                                                     @Validated({UpdateWish.class}) @RequestBody CommunityRequestBody requestBody,
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
        Community community = repository.getById(id);
        if (!community.getUser().equals(user) && !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new CommunityAccessDenied();
        }

        // ?????????????????????????????????????????????????????????null??????????????????????????????
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            requestBody.setState(null);
        }

        communityMapper.updateCommunityFromCommunityRequestBody(requestBody, community);
        community = repository.save(community);

        return communityMapper.communityToCommunitySaveResponseBody(community);
    }

    @GetMapping({"", "/"})
    public Page<CommunitySimple> getCommunities(@ApiIgnore Authentication authentication,
                                                @ApiParam(value = "??????") @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
                                                @ApiParam(value = "??????") @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
                                                @ApiParam(value = "????????????") @RequestParam(value = "user", required = false, defaultValue = "-1") Long userId,
                                                @ApiParam(value = "??????", allowableValues = "0,1,2") @RequestParam(required = false) Integer state,
                                                @ApiParam(value = "????????????") @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                                @ApiParam(value = "?????????????????????") @RequestParam(required = false, defaultValue = "false") Boolean admin,
                                                @ApiParam(value = "????????????") @RequestParam(value = "participant", required = false, defaultValue = "-1") Long participantId,
                                                @RequestParam(value = "mentor", required = false, defaultValue = "-1") Long mentorId,
                                                @RequestParam(value = "semester", required = false, defaultValue = "-1") Integer semester) throws CommunityException {
        //TODO ?????????????????? ??? ?????????????????????
        User user = (User) authentication.getPrincipal();
        admin = user.getAuthorities().contains(GlobalAuthority.ADMIN) && admin;
        if (admin && state != null) {
            if (state < 0 || state >= 3) {
                throw new CommunityIllegalParameterException("state????????????");
            }
        }

        Semester currentSemester = Semester.valuesOf(honorConfigRepository.findAll().get(0).getSemester());

        boolean finalAdmin = admin;
        Specification<Community> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type >= 0) {
                list.add(cb.equal(root.get("type").get("id"), type));
            }
            if (finalAdmin && state != null) {
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

        Pageable pageable = PageRequest.of(pageNumber, 25, sort);

        Page<Community> pages = repository.findAll(specification, pageable);

        return new PageImpl<>(communityMapper.communityToCommunitySimple(pages.getContent()), pageable, pages.getTotalElements());
    }

    @GetMapping("/{id}")
    public CommunityDetail getCommunity(@ApiIgnore Authentication authentication,
                                        @ApiParam(value = "??????") @PathVariable Long id) throws CommunityException {
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
                                                     @ApiParam(value = "??????") @PathVariable Long id) throws CommunityException {
        Community community = communityUtil.communityIsExist(id, authentication);
        repository.delete(community);
        return new GlobalResponseEntity<>("");
    }

    @GetMapping("/type")
    @ApiOperation("????????????")
    public List<CommunityType> getType() {
        return typeRepository.findAll();
    }

    @PostMapping("/type")
    @ApiOperation("????????????")
    @Secured({"ROLE_ADMIN"})
    public CommunityType createType(@ApiParam(value = "??????????????????") @RequestParam(value = "typeName") String typeName) throws CommunityException {
        if (typeRepository.existsByName(typeName)) {
            throw new CommunityIllegalParameterException("??????????????? " + typeName + " ?????????");
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
            throw new CommunityIllegalParameterException("type ?????????");
        }
        if (optionalCommunityType.get().getCount() > 0) {
            throw new CommunityIllegalParameterException("???????????????????????????0");
        }
        typeRepository.delete(optionalCommunityType.get());
        return new GlobalResponseEntity<>("ok");
    }

    @GetMapping("/participant/{id}")
    @ApiOperation("???????????????")
    public CommunityParticipantResponse getParticipant(@PathVariable Long id) throws CommunityException {
        Optional<Community> community = repository.findById(id);
        if (community.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        return communityMapper.communityToCommunityParticipantResponse(community.get());
    }

    @PostMapping("/participant/{id}/export")
    @ApiOperation("???????????????")
    public void exportParticipant(@PathVariable Long id, HttpServletResponse response, @RequestParam(value = "ids", required = false) List<Long> userIds) throws CommunityException, IOException {
        Optional<Community> optionalCommunity = repository.findById(id);
        if (optionalCommunity.isEmpty()) {
            throw new CommunityNotFoundException();
        }
        Community                  community       = optionalCommunity.get();
        List<CommunityParticipant> participants    = new ArrayList<>(community.getParticipants());
        DateFormat                 dateFormatter   = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String                     currentDateTime = dateFormatter.format(new Date());
        String                     headerValue     = "attachment; filename=users_" + currentDateTime + ".xlsx";

        participants.addAll(community.getMentors());
        if (userIds != null && userIds.size() > 0) {
            participants.removeIf(p -> !userIds.contains(p.getUser().getId()));
        }
        response.setHeader("Content-Disposition", headerValue);

        CommunityParticipantExport.valueOf(participants).export(response);
    }

    @PostMapping("/join")
    @ApiOperation("???????????????")
    public GlobalResponseEntity<String> joinCommunity(@ApiIgnore Authentication authentication,
                                                      @RequestParam(value = "id") Long id,
                                                      @RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
                                                      @RequestParam(value = "delete", required = false, defaultValue = "1") Boolean delete) throws CommunityException {

        //???????????????????????????
        //???????????????????????????
        Community community = communityUtil.communityIsExist(id, authentication);
        if (!delete) {
            if (!community.getEnrolling()) {
                throw new CommunityIllegalParameterException("????????????");
            }
            //???????????????????????? ????????????register???1
            //???????????????????????????
            //??????????????????????????????????????????
            else if (type == 0 && community.getLimit() <= community.getParticipantsCount() && community.getLimit() > 0 && community.getRegistrationType() == 0) {
                throw new CommunityIllegalParameterException("????????????????????????");
            }
            if (type == 1 && community.getMentors().size() >= 2) {
                throw new CommunityIllegalParameterException("????????????????????????");
            }
            if (type != 0 && type != 1) {
                throw new CommunityIllegalParameterException("??????????????????");
            }
            //???????????????
            User    user          = (User) authentication.getPrincipal();
            boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.getUser().equals(user));
            boolean isMentor      = community.getMentors().stream().anyMatch(x -> x.getUser().equals(user));
            if (isParticipant || isMentor) {
                if (community.getRegistrationType() == 0) {
                    throw new CommunityIllegalParameterException("????????????");
                } else {
                    CommunityParticipant cp = communityParticipantRepository.findCommunityParticipantByUserAndCommunityId(user, id);
                    if (cp.getValid()) {
                        throw new CommunityIllegalParameterException("????????????");
                    } else throw new CommunityIllegalParameterException("??????????????????");
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
                throw new CommunityIllegalParameterException("????????????????????????");
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
    @ApiOperation("??????")
    public GlobalResponseEntity<String> approveJoin(@ApiIgnore Authentication authentication,
                                                    @RequestParam(value = "communityId") Long communityId,
                                                    @RequestParam(value = "type", required = false, defaultValue = "1") Boolean type,
                                                    @RequestParam(value = "userId", required = false, defaultValue = "-1") List<Long> userIds) throws CommunityException {
        //???????????????????????????
        Community community = communityUtil.communityIsExist(communityId, authentication);

        //???????????????
        User auth = (User) authentication.getPrincipal();
        if (!auth.equals(community.getUser()))
            throw new CommunityIllegalParameterException("??????????????????");

        Specification<CommunityParticipant> specification = (root, query, cb) -> cb.and(cb.equal(root.get("communityId"), community.getId()), root.get("user").get("id").in(userIds));
        List<CommunityParticipant>          cpList        = communityParticipantRepository.findAll(specification);

        for (CommunityParticipant participant : cpList) {
            participant.setValid(type);
        }
        //??????
        communityParticipantRepository.saveAll(cpList);
        return new GlobalResponseEntity<>(0, "????????????");
    }

    @DeleteMapping("/participant")
    @ApiOperation("???????????????")
    public GlobalResponseEntity<String> delParticipant(@ApiIgnore Authentication authentication,
                                                       @RequestParam(value = "communityId") Long communityId,
                                                       @ApiParam(value = "???????????????") @RequestParam(value = "ids", required = false, defaultValue = "-1") Set<Long> ids) throws CommunityException {

        //???????????????????????????
        Community community = communityUtil.communityIsExist(communityId, authentication);
        //??????????????????
        User auth = (User) authentication.getPrincipal();
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN) && !auth.equals(community.getUser())) {
            throw new CommunityIllegalParameterException("???????????????");
        }

        community.removeParticipant(ids);
        repository.save(community);

        return new GlobalResponseEntity<>(0, "????????????");
    }

    @PostMapping("/rec")
    @ApiOperation("????????????")
    public CommunityRecord createRecord(@RequestBody CommunityRecordRequestBody recordRequestBody,
                                        @ApiIgnore Authentication authentication) throws CommunityException {
        Long communityId = recordRequestBody.getCommunityId();
        //???????????????????????????
        Community community = communityUtil.communityIsExist(communityId, authentication);

        //???????????????????????????????????????
        User    auth          = (User) authentication.getPrincipal();
        boolean isParticipant = community.getParticipants().stream().anyMatch(x -> x.getUser().equals(auth));
        boolean isMentor      = community.getMentors().stream().anyMatch(x -> x.getUser().equals(auth));

        if (!isMentor && !isParticipant) {
            throw new CommunityIllegalParameterException("???????????????");
        }


        CommunityRecord communityRecord = new CommunityRecord();
        communityMapper.updateCommunityRecordFromCommunityRecordRequestBody(recordRequestBody, communityRecord);
        communityRecord.setUser(auth);
        communityRecord.setCreateTime(new Date());

        //return communityRecord;
        return communityRecordRepository.save(communityRecord);
    }

    @GetMapping("/rec/{id}")
    @ApiOperation("????????????")
    public List<CommunityRecordDto> getRecord(@ApiParam(value = "???????????????") @PathVariable(value = "id") Long communityId,
                                              @ApiIgnore Authentication authentication) throws CommunityException {
        //???????????????????????????
        Community community = communityUtil.communityIsExist(communityId, authentication);

        return communityMapper.communityRecordToCommunityRecordDto(communityRecordRepository.findAllByCommunity(community));
    }

    @GetMapping("/rec")
    @ApiOperation("????????????")
    @Secured("ROLE_ADMIN")
    public Page<CommunityRecordDto> getRecords(@RequestParam(value = "page", defaultValue = "0", required = false) Integer page) {
        Sort                  sort                = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable              pageable            = PageRequest.of(page, 25, sort);
        Page<CommunityRecord> communityRecordPage = communityRecordRepository.findAll(pageable);
        return communityRecordPage.map(communityRecord -> communityMapper.communityRecordToCommunityRecordDto(communityRecord));
    }

    //TODO: ???????????????????????????????????????????????????????????????
    @DeleteMapping("/record")
    @ApiOperation("????????????")
    public GlobalResponseEntity<String> deleteRecord(@ApiParam(value = "????????????") @RequestParam List<Integer> ids,
                                                     @ApiIgnore Authentication authentication) throws CommunityException {

        List<CommunityRecord> communityRecordList = communityRecordRepository.findAllById(ids);
        if (communityRecordList.isEmpty()) {
            throw new CommunityIllegalParameterException("???????????????");
        }

        User auth = (User) authentication.getPrincipal();
        //??????????????????
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            //??????????????????????????????????????????????????????
            communityRecordList = communityRecordList.stream().filter(a -> Objects.equals(a.getUser().getId(), auth.getId())).collect(Collectors.toList());
            if (communityRecordList.isEmpty()) {
                throw new CommunityIllegalParameterException("????????????????????????");
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
}
