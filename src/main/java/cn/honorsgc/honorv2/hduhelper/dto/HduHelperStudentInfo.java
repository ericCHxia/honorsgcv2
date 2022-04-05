package cn.honorsgc.honorv2.hduhelper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class HduHelperStudentInfo implements Serializable {
    @JsonProperty("CLASSID")
    private String classId;
    @JsonProperty("DORMBUILDING")
    private String dormBuilding;
    @JsonProperty("DORMROOM")
    private String dormRoom;
    @JsonProperty("MAJORCODE")
    private String majorCode;
    @JsonProperty("MAJORNAME")
    private String majorName;
    @JsonProperty("RUXUESJ")
    private String enrollmentDate;
    @JsonProperty("STAFFID")
    private String staffId;
    @JsonProperty("STAFFNAME")
    private String staffName;
    @JsonProperty("TEACHERID")
    private String teacherId;
    @JsonProperty("TEACHERNAME")
    private String teacherName;
    @JsonProperty("UNITCODE")
    private String unitCode;
    @JsonProperty("UNITNAME")
    private String unitName;
}
