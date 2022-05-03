package cn.honorsgc.honorv2.community.entity;

import cn.honorsgc.honorv2.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "gttatd")
public class CommunityAttend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usrid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "recid")
    private CommunityRecord record;
}
