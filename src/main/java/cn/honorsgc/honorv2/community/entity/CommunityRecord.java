package cn.honorsgc.honorv2.community.entity;

import cn.honorsgc.honorv2.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "gttrec")
public class CommunityRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usrid", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gttid")
    private Community community;

    @NotNull
    @Column(name = "detail", length = 1000)
    private String detail;

    @Column(name = "img", length = 500)
    private String cover;

    @Column(name = "tim")
    private Date createTime;

    @JoinTable(name = "gttatd",joinColumns = @JoinColumn(name = "recid"),inverseJoinColumns = @JoinColumn(name = "usrid"))
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<User> attendant;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<User> getAttendant() {
        return attendant;
    }

    public void setAttendant(List<User> users) {
        this.attendant = users;
    }
}