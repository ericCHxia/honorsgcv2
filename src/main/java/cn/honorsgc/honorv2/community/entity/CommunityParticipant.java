package cn.honorsgc.honorv2.community.entity;

import cn.honorsgc.honorv2.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "gttptcp")
public class CommunityParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "usrid", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gttid")
    @Getter
    @Setter
    private Community community;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof User)return Objects.equals(user, o);
        if (o == null || getClass() != o.getClass()) return false;
        CommunityParticipant that = (CommunityParticipant) o;
        return Objects.equals(user, that.user) && Objects.equals(community, that.community);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, community);
    }
}