package cn.honorsgc.honorv2.article.dto;

import cn.honorsgc.honorv2.user.dto.UserDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleCommentDto implements Serializable {
    private final Integer id;
    private final UserDto user;
    private final String detail;
    private final Date createTime;
}
