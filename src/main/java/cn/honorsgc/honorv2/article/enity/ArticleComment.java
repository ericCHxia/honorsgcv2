package cn.honorsgc.honorv2.article.enity;

import cn.honorsgc.honorv2.user.User;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "cmt")
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usrid", referencedColumnName = "id")
    private User user;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "contid")
    private Long contentId;

    @Column(name = "detail", length = 1000)
    private String detail;

    @Column(name = "tim")
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User users) {
        this.user = users;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}