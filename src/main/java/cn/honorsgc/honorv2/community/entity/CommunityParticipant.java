package cn.honorsgc.honorv2.community.entity;

import cn.honorsgc.honorv2.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "gttptcp")
public class CommunityParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usrid", referencedColumnName = "id")
    private User users;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "gttid")
    private Long communityId;

    @Column(name = "typ")
    private Integer type;

    @Column(name = "valid")
    private Boolean valid;

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityParticipant that = (CommunityParticipant) o;
        return Objects.equals(users, that.users) && Objects.equals(communityId, that.communityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, communityId);
    }
}