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
import java.util.Map;

public class CommunityParticipantExport {
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final Map<CommunityParticipant, Integer> participantCount;
    private final Integer recordCount;
    private final String[] headers = {"姓名", "学号", "学院", "专业", "身份", "有效性", "参与次数", "到勤率"};

    public CommunityParticipantExport(Map<CommunityParticipant, Integer> participantCount, Integer recordCount) {
        this.participantCount = participantCount;
        this.recordCount = recordCount;
        workbook = new XSSFWorkbook();
    }

    public static CommunityParticipantExport valueOf(Map<CommunityParticipant, Integer> participantCount, Integer recordCount) {
        return new CommunityParticipantExport(participantCount, recordCount);
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("sheet1");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setBold(true);
        font.setFontHeight(11);
        font.setFontName("等线");
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
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

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        font.setFontName("等线");
        style.setFont(font);

        CellStyle percentageStyle = workbook.createCellStyle();
        percentageStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        percentageStyle.setFont(font);

        for (Map.Entry<CommunityParticipant, Integer> participantEntry : participantCount.entrySet()) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            CommunityParticipant participant = participantEntry.getKey();
            createCell(row, columnCount++, participant.getUser().getName(), style);
            createCell(row, columnCount++, participant.getUser().getUserId(), style);
            createCell(row, columnCount++, participant.getUser().getCollege(), style);
            createCell(row, columnCount++, participant.getUser().getSubject(), style);
            createCell(row, columnCount++, participant.getType() == 1 ? "指导者" : "参与者", style);
            createCell(row, columnCount++, participant.getValid() ? "有效" : "待通过", style);
            createCell(row, columnCount++, participantEntry.getValue(), style);
            createCell(row, columnCount, Double.valueOf(participantEntry.getValue()) / Double.valueOf(recordCount), percentageStyle);
        }
    }
}
