package hooks;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.cucumber.java.Scenario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    private static Scenario currentScenario;

    // Set by Hooks class
    public static void setScenario(Scenario scenario) {
        currentScenario = scenario;
    }


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
    
    public static String getData(String sheetName,String columnName) throws IOException {
    	String filePath = "./Datasheet.xlsx";

        if (currentScenario == null) {
            throw new IllegalStateException("Scenario not set. Use setScenario() in @Before hook.");
        }

        String scenarioName = currentScenario.getName().trim();
        System.out.println("current sceanrio name for getData - " + scenarioName);

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in file: " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("No header row found in sheet: " + sheetName);
            }

            int scenarioColumnIndex = -1;
            int desiredColumnIndex = -1;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = headerRow.getCell(i).getStringCellValue().trim();
                if (header.equalsIgnoreCase("Scenario")) {
                    scenarioColumnIndex = i;
                }
                if (header.equalsIgnoreCase(columnName)) {
                    desiredColumnIndex = i;
                }
            }

            if (scenarioColumnIndex == -1 || desiredColumnIndex == -1) {
                throw new IllegalArgumentException("Required columns not found in the sheet.");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell scenarioCell = row.getCell(scenarioColumnIndex);
                if (scenarioCell != null && scenarioCell.getStringCellValue().trim().equalsIgnoreCase(scenarioName)) {
                    Cell targetCell = row.getCell(desiredColumnIndex);
                    return (targetCell != null) ? targetCell.toString().trim() : "";
                }
            }

            throw new IllegalArgumentException("Scenario '" + scenarioName + "' not found in sheet: " + sheetName);
        }
    }
}