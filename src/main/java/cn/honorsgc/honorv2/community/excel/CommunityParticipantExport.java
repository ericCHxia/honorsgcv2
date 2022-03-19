package cn.honorsgc.honorv2.community.excel;

import cn.honorsgc.honorv2.community.entity.CommunityParticipant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CommunityParticipantExport {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<CommunityParticipant> participantList;
    private final String[] headers = {"姓名","学号","学院","专业","身份","有效性"};

    public CommunityParticipantExport(List<CommunityParticipant> participantList) {
        this.participantList = participantList;
        workbook = new XSSFWorkbook();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer){
            cell.setCellValue((Integer) value);
        }else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeHeaderLine(){
        sheet = workbook.createSheet("sheet1");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setBold(true);
        font.setFontHeight(11);
        font.setFontName("等线");
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            createCell(row,i,headers[i],style);
        }
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        font.setFontName("等线");
        style.setFont(font);

        for(CommunityParticipant participant:participantList){
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, participant.getUser().getName(), style);
            createCell(row, columnCount++, participant.getUser().getUserId(), style);
            createCell(row, columnCount++, participant.getUser().getCollege(), style);
            createCell(row, columnCount++, participant.getUser().getSubject(), style);
            createCell(row, columnCount++, participant.getType()==1?"指导者":"参与者", style);
            createCell(row, columnCount, participant.getValid()?"有效":"待通过", style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        response.setContentType("application/octet-stream");
        outputStream.close();
    }

    public static CommunityParticipantExport valueOf(List<CommunityParticipant> participants){
        return new CommunityParticipantExport(participants);
    }
}
