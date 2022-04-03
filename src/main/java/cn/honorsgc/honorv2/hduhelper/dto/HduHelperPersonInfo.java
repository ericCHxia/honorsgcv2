package cn.honorsgc.honorv2.hduhelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HduHelperPersonInfo implements Serializable {
    @JsonIgnore
    public final static String StudentType = "1";
    @JsonIgnore
    public final static String TeacherType = "2";

    @JsonProperty("GRADE")
    private String grade;
    @JsonProperty("STAFFID")
    private String staffId;
    @JsonProperty("STAFFNAME")
    private String staffName;
    @JsonProperty("STAFFSTATE")
    private String staffState;
    @JsonProperty("STAFFTYPE")
    private String staffType;
    @JsonProperty("UNITCODE")
    private String unitCode;
    @JsonProperty("UNITNAME")
    private String unitName;
}
