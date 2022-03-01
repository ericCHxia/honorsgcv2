package cn.honorsgc.honorv2.community.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommunitySaveResponseBody implements Serializable {
    private final Long id;
    private final String title;
    private final Integer state;
    private final Date createDate;
}
