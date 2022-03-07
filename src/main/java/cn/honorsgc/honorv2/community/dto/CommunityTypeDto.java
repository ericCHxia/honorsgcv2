package cn.honorsgc.honorv2.community.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommunityTypeDto implements Serializable {
    private final Integer id;
    private final String name;
}
