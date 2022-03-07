package cn.honorsgc.honorv2.article.repository;

import cn.honorsgc.honorv2.article.enity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Integer>, JpaSpecificationExecutor<ArticleComment> {
    List<ArticleComment> findArticleCommentsByArticle_Id(Long id);
    Integer deleteAllByIdIn(List<Integer> ids);
}