package cn.honorsgc.honorv2.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ArticleCommentResponse implements Serializable {
    private final Long articleId;
    private final List<ArticleCommentDto> comments;
    public static ArticleCommentResponse valuesOf(List<ArticleCommentDto> comments){
        Long articleId = null;
        if (comments.size()>0){
            articleId = comments.get(0).getContentId();
        }
        return new ArticleCommentResponse(articleId,comments);
    }
}
