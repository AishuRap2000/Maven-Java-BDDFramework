package hooks;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.cucumber.java.Scenario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Holds a reference to the current Cucumber scenario being executed.
public class Helper {

	private static Scenario currentScenario;

	// Set by Hooks class
	// setScenario is called likely from a Cucumber @Before hook to assign the
	// running scenario to this static variable
	public static void setScenario(Scenario scenario) {
		currentScenario = scenario;
	}

	/*
	 * Reads the Excel sheet to find which scenarios are marked with "Yes" in the
	 * run flag column. Returns a list of scenario names to execute. Assumes the
	 * first row is the header. Uses columns:
	 * 
	 * Column 1 (index 1): Scenario Name
	 * 
	 * Column 2 (index 2): Run flag ("Yes"/"No")
	 */

	public static List<String> getScenariosToRun(String filePath) {
		List<String> scenariosToRun = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip header row

				String scenarioName = getCellValue(row.getCell(1)).trim(); // Scenario name from 2nd column
				String runFlag = getCellValue(row.getCell(2)).trim(); // Run flag from 3rd column

				if ("Yes".equalsIgnoreCase(runFlag)) {
					scenariosToRun.add(scenarioName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return scenariosToRun;
	}

	/*
	 * Updates the Excel sheet to write the test status (e.g., Pass/Fail) for a
	 * given scenario Finds the matching scenario by name and updates the 4th column
	 * Writes changes back to the same Excel file
	 */
	public static void writeScenarioStatus(String filePath, String scenarioName, String status) {
		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue; // Skip header

				String currentScenario = getCellValue(row.getCell(1)).trim();
				if (currentScenario.equalsIgnoreCase(scenarioName)) {
					Cell statusCell = row.getCell(3); // Status column (4th col)
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

	/*
	 * A helper method that reads a Cell from Excel and returns its content as a
	 * string Handles different cell types: String, Numeric, Boolean, Formula, Blank
	 * Converts numeric cells to long before string conversion to avoid decimal
	 * point issues
	 */
	public static String getCellValue(Cell cell) {
		if (cell == null)
			return "";
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

	/*
	 * r eads the Excel sheet named sheetName in the Datasheet.xlsx file. Finds the
	 * row matching the current scenario and retrieves data from the specified
	 * column. columnName corresponds to the header name in the Excel sheet. Throws
	 * exceptions if:
	 * 
	 * Scenario is not set (not called from inside a scenario)
	 * 
	 * Sheet or columns not found
	 * 
	 * Scenario name not found in the data sheet
	 * 
	 * 
	 */
	public static String getData(String sheetName, String columnName) throws IOException {
		String filePath = "./Datasheet.xlsx";

		if (currentScenario == null) {
			throw new IllegalStateException("Scenario not set");
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
				if (row == null)
					continue;

				Cell scenarioCell = row.getCell(scenarioColumnIndex);
				if (scenarioCell != null && scenarioCell.getStringCellValue().trim().equalsIgnoreCase(scenarioName)) {
					Cell targetCell = row.getCell(desiredColumnIndex);
					return (targetCell != null) ? targetCell.toString().trim() : "";
				}
			}

			throw new IllegalArgumentException("Scenario '" + scenarioName + "' not found in sheet: " + sheetName);
		}
	}
	/*
	 * A specified column name in the given sheet name,
	 * The value to be written
	 * 
	 */
	public static void putData(String sheetName, String columnName, String value) throws IOException {
	    String filePath = "./Datasheet.xlsx";

	    if (currentScenario == null) {
	        throw new IllegalStateException("Scenario not set. Use setScenario() in @Before hook.");
	    }

	    String scenarioName = currentScenario.getName().trim();
	    System.out.println("Current scenario name for putData - " + scenarioName);

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
	        int targetColumnIndex = -1;

	        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
	            String header = headerRow.getCell(i).getStringCellValue().trim();
	            if (header.equalsIgnoreCase("Scenario")) {
	                scenarioColumnIndex = i;
	            }
	            if (header.equalsIgnoreCase(columnName)) {
	                targetColumnIndex = i;
	            }
	        }

	        if (scenarioColumnIndex == -1 || targetColumnIndex == -1) {
	            throw new IllegalArgumentException("Required columns not found in the sheet.");
	        }

	        boolean scenarioFound = false;
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue;

	            Cell scenarioCell = row.getCell(scenarioColumnIndex);
	            if (scenarioCell != null && scenarioCell.getStringCellValue().trim().equalsIgnoreCase(scenarioName)) {
	                Cell targetCell = row.getCell(targetColumnIndex);
	                if (targetCell == null) {
	                    targetCell = row.createCell(targetColumnIndex);
	                }
	                targetCell.setCellValue(value);
	                scenarioFound = true;
	                break;
	            }
	        }

	        if (!scenarioFound) {
	            throw new IllegalArgumentException("Scenario '" + scenarioName + "' not found in sheet: " + sheetName);
	        }

	        // Write changes back to file
	        try (FileOutputStream fos = new FileOutputStream(filePath)) {
	            workbook.write(fos);
	        }

	    }
	}

}