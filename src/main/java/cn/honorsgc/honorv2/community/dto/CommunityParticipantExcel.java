package cn.honorsgc.honorv2.community.dto;

import cn.honorsgc.honorv2.community.excel.ParticipantTypeConverter;
import cn.honorsgc.honorv2.community.excel.ValidExcelConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import lombok.Data;

@HeadFontStyle(fontHeightInPoints = 10)
@Data
public class CommunityParticipantExcel {
    @ExcelProperty("姓名")
    private final String name;
    @ExcelProperty("学号")
    private final String id;
    @ExcelProperty("学院")
    private final String college;
    @ColumnWidth(20)
    @ExcelProperty("共同体名称")
    private final String communityName;
    @ColumnWidth(10)
    @ExcelProperty("共同体类型")
    private final String communityType;
    @ExcelProperty(value = "身份",converter = ParticipantTypeConverter.class)
    private final Integer type;
    @ExcelProperty(value = "有效性",converter = ValidExcelConverter.class)
    private final Boolean valid;
    @ExcelProperty(value = "参加次数")
    private final Long attendCount;
    @ExcelProperty(value = "总次数")
    private final int total;
}