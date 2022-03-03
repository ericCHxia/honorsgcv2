package cn.honorsgc.honorv2.article;

import cn.honorsgc.honorv2.article.dto.ArticleCommentDto;
import cn.honorsgc.honorv2.article.enity.ArticleComment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ArticleMapper {
    ArticleCommentDto articleCommentToArticleCommentDto(ArticleComment articleComment);
    List<ArticleCommentDto> articleCommentToArticleCommentDto(List<ArticleComment> articleComment);
}
