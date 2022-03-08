package cn.honorsgc.honorv2.article.dto;

import cn.honorsgc.honorv2.user.dto.UserDto;
import com.sun.istack.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArticleDto implements Serializable {
    private final Long id;
    private final UserDto user;
    private final TagDto tag;
    @NotNull
    private final String title;
    private final String describe;
    private final Integer state;
    private final Date createTime;
    private final String detail;
    private final Integer type;
    @NotNull
    private final Boolean haveComment;

    @Data
    public static class TagDto implements Serializable {
        private final Long id;
        private final String name;
    }
}
