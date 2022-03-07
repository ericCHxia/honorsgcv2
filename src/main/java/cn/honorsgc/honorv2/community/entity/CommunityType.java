package cn.honorsgc.honorv2.community.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "gtttype")
public class CommunityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "type")
    private List<Community> communities;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount(){
        return this.communities.size();
    }
}
