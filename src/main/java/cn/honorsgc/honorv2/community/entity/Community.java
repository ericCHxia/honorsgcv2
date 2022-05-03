package cn.honorsgc.honorv2.community.entity;

import cn.honorsgc.honorv2.core.Semester;
import cn.honorsgc.honorv2.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "gttdata")
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "usrid")
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "typ")
    private CommunityType type;

    private String title;

    @Column(name = "dsc")
    private String describe;

    private String detail;

    private String img;

    @Column(name = "lmt")
    private Integer limit;

    @Column(name = "stat")
    private Integer state;

    @Column(name = "tim")
    private Date createDate;

    private Boolean enrolling;

    @Column(name = "need_mentor")
    private Boolean needMentor;

    @Column(name = "registration")
    private Integer registrationType;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gttid")
    @Where(clause = "typ = 0")
    Set<CommunityParticipant> participants;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gttid")
    @Where(clause = "typ = 1")
    Set<CommunityParticipant> mentors;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "gttid")
    @Getter
    @Setter
    List<CommunityRecord> records;

    public Integer getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(Integer registrationType) {
        this.registrationType = registrationType;
    }

    public Boolean getNeedMentor() {
        return needMentor;
    }

    public void setNeedMentor(Boolean needMentor) {
        this.needMentor = needMentor;
    }

    public Integer getParticipantsCount() {
        return participants.size();
    }

    public Set<CommunityParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<CommunityParticipant> participants) {
        this.participants = participants;
    }

    public Set<CommunityParticipant> getMentors() {
        return mentors;
    }

    public void setMentors(Set<CommunityParticipant> mentors) {
        this.mentors = mentors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CommunityType getType() {
        return type;
    }

    public void setType(CommunityType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getEnrolling() {
        return enrolling;
    }

    public void setEnrolling(Boolean enrolling) {
        this.enrolling = enrolling;
    }

    public void removeParticipant(Set<Long> userIds) {
        participants.removeIf(e -> userIds.contains(e.getUser().getId()));
        mentors.removeIf(e -> userIds.contains(e.getUser().getId()));
    }

    public Semester getSemester() {
        return Semester.valuesOf(createDate);
    }
}
