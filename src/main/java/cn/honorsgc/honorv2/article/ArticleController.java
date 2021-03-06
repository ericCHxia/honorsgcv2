package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.dto.*;
import cn.honorsgc.honorv2.article.enity.Article;
import cn.honorsgc.honorv2.article.enity.ArticleComment;
import cn.honorsgc.honorv2.article.enity.Tag;
import cn.honorsgc.honorv2.article.expection.*;
import cn.honorsgc.honorv2.article.repository.ArticleCommentRepository;
import cn.honorsgc.honorv2.article.repository.ArticleRepository;
import cn.honorsgc.honorv2.article.repository.TagRepository;
import cn.honorsgc.honorv2.core.CreateWish;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ArticleCommentRepository articleCommentRepository;
    @Autowired
    private ArticleMapper mapper;

    @GetMapping({"", "/"})
    @ApiOperation(value = "查找文章")
    public Page<ArticleSimple> index(@ApiIgnore Authentication authentication, @ApiParam(value = "页号") @RequestParam(value = "page", required = false, defaultValue = "0") Integer pageNumber,
                                     @ApiParam(value = "类型") @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
                                     @ApiParam(value = "用户编号") @RequestParam(value = "user", required = false, defaultValue = "-1") Integer userId,
                                     @ApiParam(value = "状态", allowableValues = "0,1,2") @RequestParam(required = false) Integer state,
                                     @ApiParam(value = "搜索文本") @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                     @ApiParam(value = "使用管理员权限") @RequestParam(required = false, defaultValue = "false") Boolean admin) throws ArticleException {
        User user = (User) authentication.getPrincipal();
        admin = user.getAuthorities().contains(GlobalAuthority.ADMIN) && admin;
        if (admin&&state != null) {
            if (state < 0 || state > 3) {
                throw new ArticleIllegalParameterException("state参数错误");
            }
        }

        Boolean finalAdmin = admin;
        Specification<Article> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type >= 0) list.add(cb.equal(root.get("type"), type));
            if (userId > 0) list.add(cb.equal(root.get("user").get("id"), userId));
            if (finalAdmin&&state != null) {
                list.add(cb.equal(root.get("state"), state));
            } else if (!finalAdmin){
                list.add(cb.or(cb.equal(root.get("state"), 1), cb.equal(root.get("user").get("id"), user.getId())));
            }

            if (!search.equals("")) list.add(cb.like(root.get("title"), "%" + search + "%"));
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        Pageable      pageable    = PageRequest.of(pageNumber, 25, sort);
        Page<Article> articlePage = articleRepository.findAll(spec, pageable);
        return articlePage.map((x) -> mapper.articleToArticleSimple(x));
    }

    @PostMapping({"", "/"})
    @ApiOperation(value = "编辑或者新建文章")
    public ArticlePostResponse postArticle(@ApiIgnore Authentication authentication, @Validated({CreateWish.class}) @RequestBody ArticleRequestBody requestBody, @ApiIgnore Errors errors) throws ArticleException {

        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().get(0);
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                throw new ArticleIllegalParameterException(fieldError.getField() + fieldError.getDefaultMessage());
            }
            throw new ArticleIllegalParameterException(objectError.getDefaultMessage());
        }

        User    user = (User) authentication.getPrincipal();
        Article newArticle;
        newArticle = new Article();
        newArticle.setUser(user);
        newArticle.setCreateTime(new Date());
        if (user.getAuthorities().contains(GlobalAuthority.ADMIN) && requestBody.getState() != null) {
            newArticle.setState(requestBody.getState());
        } else {
            newArticle.setState(ArticleState.notApproved);
        }

        postArticleDo(requestBody, newArticle);
        Article savedArticle = articleRepository.save(newArticle);
        return new ArticlePostResponse("https://", savedArticle.getId());
    }

    @PutMapping({"/{id}"})
    public ArticlePostResponse updateArticle(@ApiIgnore Authentication authentication, @PathVariable Long id, @RequestBody ArticleRequestBody requestBody, @ApiIgnore Errors errors) throws ArticleException {
        if (errors.hasErrors()) {
            ObjectError objectError = errors.getAllErrors().get(0);
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                throw new ArticleIllegalParameterException(fieldError.getField() + fieldError.getDefaultMessage());
            }
            throw new ArticleIllegalParameterException(objectError.getDefaultMessage());
        }
        Optional<Article> oldArticle = articleRepository.findById(id);
        if (oldArticle.isEmpty()) {
            throw new ArticleNotFoundException();
        }
        User user = (User) authentication.getPrincipal();
        if (!user.equals(oldArticle.get().getUser()) && !user.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new ArticleAccessDeniedException();
        }
        Article article = oldArticle.get();
        if (user.getAuthorities().contains(GlobalAuthority.ADMIN) && requestBody.getState() != null) {
            article.setState(requestBody.getState());
        } else {
            article.setState(ArticleState.notApproved);
        }
        postArticleDo(requestBody, article);
        Article savedArticle = articleRepository.save(article);
        return new ArticlePostResponse("https://", savedArticle.getId());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取文章")
    public ArticleDto getArticle(@ApiIgnore Authentication authentication, @PathVariable Long id) throws ArticleNotFoundException {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isEmpty()) {
            throw new ArticleNotFoundException();
        }
        User user = (User) authentication.getPrincipal();
        if (article.get().getUser().equals(user)) return mapper.articleToArticleDto(article.get());
        if (article.get().getState() != 1 && !authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            throw new ArticleNotFoundException();
        }
        return mapper.articleToArticleDto(article.get());
    }

    public void postArticleDo(ArticleRequestBody requestBody, Article article) throws ArticleIllegalParameterException {
        if (requestBody.getTag() != null) {
            Optional<Tag> tag = tagRepository.findById(requestBody.getTag());
            if (tag.isEmpty()) {
                throw new ArticleIllegalParameterException("tag不存在");
            }
            article.setTag(tag.get());
        }

        if (requestBody.getType() != null) article.setType(requestBody.getType());
        if (requestBody.getTitle() != null) article.setTitle(requestBody.getTitle());
        if (requestBody.getDetail() != null) article.setDetail(requestBody.getDetail());
        if (requestBody.getDescribe() != null) article.setDescribe(requestBody.getDescribe());
        if (requestBody.getHaveComment() != null) article.setHaveComment(requestBody.getHaveComment());
    }

    @GetMapping("/change_state")
    @Secured({"ROLE_ADMIN"})
    @ApiOperation(value = "修改文章状态")
    public GlobalResponseEntity<String> changeArticleState(@ApiParam(value = "编号", required = true) @RequestParam Long id, @ApiParam(value = "状态", required = true, allowableValues = "0,1,2") @RequestParam Integer state) throws ArticleException {
        Optional<Article> article = articleRepository.findById(id);
        if (article.isEmpty()) {
            throw new ArticleIllegalParameterException("articleId 不存在");
        }
        Article newArticle = article.get();
        newArticle.setState(state);
        articleRepository.save(newArticle);
        return new GlobalResponseEntity<>();
    }

    //TODO: 单文章多标签
    @GetMapping("/tag")
    @ApiOperation(value = "获取文章标签")
    public List<Tag> getTags(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "0") boolean admin, @ApiIgnore Authentication authentication) {
        admin = admin && authentication.getAuthorities().contains(GlobalAuthority.ADMIN);
        if (admin) {
            return tagRepository.findAll();
        }
        if (search.equals("")) {
            return tagRepository.findAll();
        } else {
            return tagRepository.findTop10ByNameIsStartingWith(search);
        }
    }

    @PostMapping("/tag")
    @ApiOperation(value = "新建标签")
    public Tag createTag(@ApiIgnore Authentication authentication, @RequestParam String name) throws ArticleException {
        Optional<Tag> tagOptional = tagRepository.findTagByName(name);
        if (tagOptional.isPresent()) {
            throw new TagIsExistException();
        }
        User user = (User) authentication.getPrincipal();
        Tag  tag  = new Tag(name, user);
        return tagRepository.save(tag);
    }

    @DeleteMapping("/tag/{id}")
    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "删除标签")
    public GlobalResponseEntity<String> deleteTag(@PathVariable Long id) throws ArticleException {
        Optional<Tag> optionalTag = tagRepository.findById(id);
        if (optionalTag.isEmpty()) {
            throw new TagIsNotFound();
        }
        if (optionalTag.get().getCount()>0){
            throw new TagCountIsNotEmpty();
        }
        tagRepository.delete(optionalTag.get());
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("delete successfully");
        return responseEntity;
    }

    @GetMapping("/cmt/{id}")
    @ApiOperation(value = "获取评论")
    public List<ArticleCommentDto> getComment(@ApiParam(value = "文章编号", required = true) @PathVariable Long id) {
        List<ArticleComment> articleCommentList = articleCommentRepository.findArticleCommentsByArticle_Id(id);
        return mapper.articleCommentToArticleCommentDto(articleCommentList);
    }

    @GetMapping("/cmt")
    @ApiOperation(value = "获取评论")
    @Secured({"ROLE_ADMIN"})
    public List<ArticleCommentAdminDto> getComments() {
        List<ArticleComment> articleComments = articleCommentRepository.findAll();
        return mapper.articleCommentToArticleCommentAdminDto(articleComments);
    }

    @PostMapping("/cmt")
    @ApiOperation(value = "发布评论")
    public ArticleComment createComment(@ApiIgnore Authentication authentication, @ApiParam(value = "文章编号", required = true) @RequestParam Long id, @ApiParam(value = "评论内容", required = true) @RequestParam String detail) throws ArticleException {
        //检查文章是否存在
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (optionalArticle.isEmpty()) {
            throw new ArticleNotFoundException();
        }
        Article article = optionalArticle.get();
        if (!article.getHaveComment()) {
            throw new ArticleIllegalParameterException("文章不允许评论");
        }

        ArticleComment articleComment = new ArticleComment();
        User           auth           = (User) authentication.getPrincipal();
        articleComment.setArticle(article);
        articleComment.setDetail(detail);
        articleComment.setUser(auth);
        articleComment.setCreateTime(new Date());
        return articleCommentRepository.save(articleComment);
    }

    @DeleteMapping("/cmt")
    @ApiOperation(value = "删除评论")
    public GlobalResponseEntity<String> deleteComment(@ApiParam(value = "评论编号") @RequestParam List<Integer> ids, @ApiIgnore Authentication authentication) throws ArticleException {

        List<ArticleComment> articleCommentList = articleCommentRepository.findAllById(ids);
        if (articleCommentList.isEmpty()) {
            throw new ArticleIllegalParameterException("没有评论");
        }

        User auth = (User) authentication.getPrincipal();
        //管理员直接删
        if (!authentication.getAuthorities().contains(GlobalAuthority.ADMIN)) {
            //若不是管理员过滤掉不是本人发布的信息
            articleCommentList = articleCommentList.stream().filter(a -> Objects.equals(a.getUser().getId(), auth.getId())).collect(Collectors.toList());
            if (articleCommentList.isEmpty()) {
                throw new ArticleIllegalParameterException("没有您发布的评论");
            }
        }
        //不根据id删，因为传来的id可能无效
        articleCommentRepository.deleteAll(articleCommentList);
        GlobalResponseEntity<String> responseEntity = new GlobalResponseEntity<>();
        responseEntity.setMessage("delete successfully");
        return responseEntity;
    }

    @DeleteMapping({"", "/"})
    @ApiOperation(value = "删除文章")
    public GlobalResponseEntity<String> deleteArticle(@ApiParam(value = "文章编号") @RequestParam List<Long> ids, @ApiIgnore Authentication authentication) throws ArticleException {

        //只有管理员才能批量删除文章
        if (ids.size() > 1 && !GlobalAuthority.isAdmin(authentication)) {
            throw new ArticleAccessDeniedException();
        }

        //判断传参的ids大于0
        if (ids.isEmpty()) {
            throw new ArticleIllegalParameterException("ids不因为空");
        }

        List<Article> articles = articleRepository.findAllById(ids);
        if (articles.isEmpty()) {
            throw new ArticleIllegalParameterException("ids是无效的");
        }

        if (!GlobalAuthority.isAdmin(authentication) && !articles.get(0).getUser().equals(authentication.getPrincipal())) {
            throw new ArticleAccessDeniedException();
        }

        articleRepository.deleteAll(articles);
        return new GlobalResponseEntity<>();
    }
}
