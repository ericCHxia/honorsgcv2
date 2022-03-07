package cn.honorsgc.honorv2.article.dto;

import cn.honorsgc.honorv2.user.dto.UserDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleCommentAdminDto implements Serializable {
    private final Integer id;
    private final UserDto user;
    private final ArticleSimple article;
    private final String detail;
    private final Date createTime;
}
