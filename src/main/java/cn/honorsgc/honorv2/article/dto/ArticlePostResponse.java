package cn.honorsgc.honorv2.article.dto;

public class ArticlePostResponse {
    private String url;
    private Long articleId;

    public ArticlePostResponse(String url, Long articleId) {
        this.url = url;
        this.articleId = articleId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
