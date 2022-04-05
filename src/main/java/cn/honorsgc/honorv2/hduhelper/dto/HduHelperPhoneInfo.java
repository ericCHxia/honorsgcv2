package cn.honorsgc.honorv2.hduhelper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HduHelperPhoneInfo implements Serializable {
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("StaffID")
    private String staffId;
}
