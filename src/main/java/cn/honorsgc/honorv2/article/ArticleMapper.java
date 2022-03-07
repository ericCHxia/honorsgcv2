package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.dto.ArticleCommentAdminDto;
import cn.honorsgc.honorv2.article.dto.ArticleCommentDto;
import cn.honorsgc.honorv2.article.dto.ArticleSimple;
import cn.honorsgc.honorv2.article.enity.Article;
import cn.honorsgc.honorv2.article.enity.ArticleComment;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ArticleMapper {
    ArticleCommentDto articleCommentToArticleCommentDto(ArticleComment articleComment);

    List<ArticleCommentDto> articleCommentToArticleCommentDto(List<ArticleComment> articleComment);

    @Mapping(source = "tag.name", target = "tagName")
    @Mapping(source = "haveComment", target = "haveComment")
    ArticleSimple articleToArticleSimple(Article article);

    List<ArticleSimple> articleToArticleSimple(List<Article> article);

    ArticleCommentAdminDto articleCommentToArticleCommentAdminDto(ArticleComment articleComment);

    List<ArticleCommentAdminDto> articleCommentToArticleCommentAdminDto(List<ArticleComment> articleComment);
}
