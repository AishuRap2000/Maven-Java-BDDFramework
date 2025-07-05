package hooks;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    public static List<String> getScenariosToRun(String filePath) {
        List<String> scenariosToRun = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String scenarioName = getCellValue(row.getCell(1)).trim(); 
                String runFlag = getCellValue(row.getCell(2)).trim();   

                if ("Yes".equalsIgnoreCase(runFlag)) {
                    scenariosToRun.add(scenarioName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scenariosToRun;
    }

    public static void writeScenarioStatus(String filePath, String scenarioName, String status) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String currentScenario = getCellValue(row.getCell(1)).trim(); 
                if (currentScenario.equalsIgnoreCase(scenarioName)) {
                    Cell statusCell = row.getCell(3); 
                    if (statusCell == null) {
                        statusCell = row.createCell(3);
                    }
                    statusCell.setCellValue(status);
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}
