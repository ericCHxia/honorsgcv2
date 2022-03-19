package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.community.validator.ValidCommunityId;
import cn.honorsgc.honorv2.community.validator.ValidCommunityTypeId;
import cn.honorsgc.honorv2.core.CreateWish;
import cn.honorsgc.honorv2.core.UpdateWish;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("共同体请求体")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommunityRequestBody implements Serializable {
    @ApiModelProperty("编号")
    @NotNull(groups = UpdateWish.class)
    @Null(groups = CreateWish.class)
    @ValidCommunityId
    private Long id;
    @ValidCommunityTypeId
    @NotNull(groups = CreateWish.class)
    private Integer typeId;
    @NotBlank(groups = CreateWish.class)
    private String title;
    @NotBlank(groups = CreateWish.class)
    private String describe;
    @NotBlank(groups = CreateWish.class)
    private String detail;
    @NotBlank(groups = CreateWish.class)
    private String img;
    @NotNull(groups = CreateWish.class)
    private Integer limit;
    @NotNull(groups = CreateWish.class)
    private Integer state;
    private Boolean enrolling;
    private Boolean needMentor;
    @NotNull(groups = CreateWish.class)
    @Range(min = 0,max = 1)
    private Integer registrationType;
}
