package cn.honorsgc.honorv2.article.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@ApiModel
public class ArticleRequestBody {
    @ApiModelProperty(value = "编号，有数据库自动生成，如果为空则为新建新的文章")
    private Long id;
    @NotNull
    @ApiModelProperty(value = "标签的Id",required = true)
    private Long tag;
    @NotNull
    @ApiModelProperty(value = "标题",required = true)
    private String title;
    @NotNull
    @ApiModelProperty(value = "文章的描述",required = true)
    private String describe;
    @NotNull
    @ApiModelProperty(value = "内容",required = true)
    private String detail;
    @ApiModelProperty(value = "类型",allowableValues="0,1",required = true)
    @NotNull
    @Max(1)
    private Integer type;

    @ApiModelProperty(value = "状态",allowableValues="0,1,2")
    @Max(2)
    private Integer state;

    @NotNull
    private Boolean haveComment;

    public Boolean getHaveComment() {
        return haveComment;
    }

    public void setHaveComment(Boolean haveComment) {
        this.haveComment = haveComment;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
