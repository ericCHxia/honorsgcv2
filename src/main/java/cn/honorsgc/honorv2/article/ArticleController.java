package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.dto.ArticlePostResponse;
import cn.honorsgc.honorv2.article.dto.ArticleRequestBody;
import cn.honorsgc.honorv2.article.enity.Article;
import cn.honorsgc.honorv2.article.enity.Tag;
import cn.honorsgc.honorv2.article.expection.*;
import cn.honorsgc.honorv2.article.repository.ArticleRepository;
import cn.honorsgc.honorv2.article.repository.TagRepository;
import cn.honorsgc.honorv2.core.GlobalAuthority;
import cn.honorsgc.honorv2.core.GlobalResponseEntity;
import cn.honorsgc.honorv2.user.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.annotation.Secured;
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

@Api(tags = "文章管理")
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleService articleService;

    @GetMapping({"","/"})
    @ApiOperation(value = "查找文章")
    public Page<Article> index(@ApiIgnore Authentication authentication,
                               @ApiParam(value = "页号") @RequestParam(value = "page",required = false,defaultValue = "0") Integer pageNumber,
                               @ApiParam(value = "类型") @RequestParam(value = "type",required = false,defaultValue = "-1") Integer type,
                               @ApiParam(value = "用户编号") @RequestParam(value = "user",required = false,defaultValue = "-1") Integer userId,
                               @ApiParam(value = "状态",allowableValues = "0,1,2") @RequestParam(required = false) Integer state,
                               @ApiParam(value = "搜索文本") @RequestParam(value = "search",required = false,defaultValue = "")String search,
                               @ApiParam(value = "使用管理员权限") @RequestParam(required = false,defaultValue = "false")Boolean admin) throws ArticleException {
        User user = (User) authentication.getPrincipal();

        if (admin&&state!=null&&user.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            if (state < 0 || state > 3) {
                throw new ArticleIllegalParameterException("state参数错误");
            }
        }

        Specification<Article> spec = (root, query, cb) ->{
            List<Predicate> list = new ArrayList<>();
            logger.warn(String.valueOf(type));
            if (type>=0)list.add(cb.equal(root.get("type"), type));
            if (userId>0)list.add(cb.equal(root.get("user").get("id"),userId));

            if (admin&&state!=null&&user.getAuthorities().contains(GlobalAuthority.ADMIN)){
                list.add(cb.equal(root.get("state"),state));
            }else {
                list.add(cb.or(cb.equal(root.get("state"),1),cb.equal(root.get("user").get("id"),user.getId())));
            }

            if (!search.equals(""))list.add(cb.like(root.get("title"),"%"+search+"%"));
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        };
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");

        Pageable pageable = PageRequest.of(pageNumber,25,sort);
        return articleRepository.findAll(spec,pageable);
    }

    @PostMapping({"","/"})
    @ApiOperation(value = "编辑或者新建文章")
    public ArticlePostResponse postArticle(@ApiIgnore Authentication authentication,
                                           @Valid @RequestBody ArticleRequestBody requestBody,
                                           @ApiIgnore Errors errors) throws ArticleException {

        if (errors.hasErrors()){
            ObjectError objectError = errors.getAllErrors().get(0);
            if(objectError instanceof FieldError){
                FieldError fieldError = (FieldError) objectError;
                throw new ArticleIllegalParameterException(fieldError.getField()+fieldError.getDefaultMessage());
            }
            throw new ArticleIllegalParameterException(objectError.getDefaultMessage());
        }
        
        User user = (User) authentication.getPrincipal();
        Article newArticle;
        if (requestBody.getId()!=null){
            Optional<Article> oldArticle = articleRepository.findById(requestBody.getId());
            if (oldArticle.isEmpty()){
                throw new ArticleNotFoundException();
            }
            if (!user.equals(oldArticle.get().getUser())&&user.getAuthorities().contains(GlobalAuthority.ADMIN)){
                throw new ArticleAccessDeniedException();
            }
            newArticle= oldArticle.get();
        }else {
            newArticle = new Article();
            newArticle.setUser(user);
            newArticle.setCreateTime(new Date());
        }
        if (user.getAuthorities().contains(GlobalAuthority.ADMIN)&&requestBody.getState()!=null){
            newArticle.setState(requestBody.getState());
        }else {
            newArticle.setState(ArticleState.notApproved);
        }

        postArticleDo(requestBody,newArticle);
        Article savedArticle = articleRepository.save(newArticle);
        return new ArticlePostResponse("https://",savedArticle.getId());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取文章")
    public Article getArticle(@ApiIgnore Authentication authentication,
                              @PathVariable Long id) throws ArticleNotFoundException {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isEmpty()){
            throw new ArticleNotFoundException();
        }
        User user = (User) authentication.getPrincipal();
        if (article.get().getUser().equals(user))return article.get();
        if (article.get().getState()!=1&&!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)){
            throw new ArticleNotFoundException();
        }
        return article.get();
    }

    public void postArticleDo(ArticleRequestBody requestBody, Article article) throws ArticleIllegalParameterException {
        Optional<Tag> tag = tagRepository.findById(requestBody.getTag());
        if (tag.isEmpty()) {
            throw new ArticleIllegalParameterException("tag不存在");
        }
        article.setType(requestBody.getType());
        article.setTag(tag.get());
        article.setTitle(requestBody.getTitle());
        article.setDetail(requestBody.getDetail());
        article.setDescribe(requestBody.getDescribe());
    }

    @GetMapping("/change_state")
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "修改文章状态")
    public GlobalResponseEntity<String> changeArticleState(@ApiParam(value = "编号",required = true) @RequestParam Long id,
                                                           @ApiParam(value = "状态",required = true,allowableValues = "0,1,2") @RequestParam Integer state) throws ArticleException{
        Optional<Article> article = articleRepository.findById(id);
        if (article.isEmpty()){
            throw new ArticleIllegalParameterException("articleId 不存在");
        }
        Article newArticle = article.get();
        newArticle.setState(state);
        articleRepository.save(newArticle);
        return new GlobalResponseEntity<>();
    }

    @GetMapping("/tag")
    @ApiOperation(value = "获取文章标签")
    public List<Tag> getTags(@RequestParam(required = false,defaultValue = "") String search){
        if (search.equals("")) {
            return articleService.getTagLimit(10);
        }else {
            return tagRepository.findTop10ByNameIsStartingWith(search);
        }
    }

    @PostMapping("/tag")
    @ApiOperation(value = "新建标签")
    public Tag createTag(@ApiIgnore Authentication authentication,
                         @RequestParam String name)throws ArticleException{
        Optional<Tag> tagOptional = tagRepository.findTagByName(name);
        if (tagOptional.isPresent()){
            throw new TagIsExistException();
        }
        User user = (User) authentication.getPrincipal();
        Tag tag = new Tag(name,user);
        return tagRepository.save(tag);
    }

    @DeleteMapping("/tag/{id}")
    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "删除标签")
    public GlobalResponseEntity<String> deleteTag(@PathVariable Long id) throws TagIsNotFound {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        if (optionalTag.isEmpty()){
            throw new TagIsNotFound();
        }
        tagRepository.delete(optionalTag.get());
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("delete successfully");
        return responseEntity;
    }
}
