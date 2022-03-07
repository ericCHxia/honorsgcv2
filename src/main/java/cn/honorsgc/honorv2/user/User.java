package cn.honorsgc.honorv2.user;

import cn.honorsgc.honorv2.core.GlobalAuthority;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "clas")
    private String classId;
    @Column(name = "num")
    private String userId;
    @Column(name = "subj")
    private String subject;
    @Column(name = "sch")
    private String college;
    private String qq;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "pwd")
    private String password;
    @Column(name = "priv")
    private int privilege;
    @Column(name = "avatar")
    private String avatar;

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public Collection<GlobalAuthority> getAuthorities() {
        List<GlobalAuthority> auths = new ArrayList<>();
        for (int i = 0; i <=privilege; i++) {
            auths.add(privilege2Authority(i));
        }
        return auths;
    }
    private GlobalAuthority privilege2Authority(int id){
        final String[] authorityName={"USER","ADMIN","SUPER_ADMIN"};
        return GlobalAuthority.valueOf(authorityName[id]);
    }

    @Override
    public String getUsername() {
        return userId;
    }

    public void setUsername(String username) {
        this.userId = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}
