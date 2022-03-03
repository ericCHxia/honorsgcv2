package cn.honorsgc.honorv2.article.repository;

import cn.honorsgc.honorv2.article.enity.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Integer>, JpaSpecificationExecutor<ArticleComment> {
}