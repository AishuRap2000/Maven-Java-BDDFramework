package hooks;

import io.cucumber.java.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileInputStream;
import java.util.*;

public class Hooks {
	/*
	 * driver: Global WebDriver instance for your tests filePath: Excel file that
	 * determines which scenarios to run allowedScenarios: Reads the Excel file and
	 * creates a list of scenarios with the "Run" flag set to Yes executedScenarios:
	 * Tracks which scenarios actually ran (helps avoid writing status for skipped
	 * scenarios).
	 */

	public static WebDriver driver;
	private static final String filePath = "./RunManager.xlsx";
	private static final List<String> allowedScenarios = getScenariosToRun(filePath);
	private static final Set<String> executedScenarios = new HashSet<>(); // to track only that really ran

	/*
	 * Registers the current scenario in the Helper class so data can be linked to
	 * it (e.g., for test data, reporting) Checks whether the scenario is in the
	 * list of allowed scenarios (RunManager.xlsx) If not, throws a
	 * PendingException, effectively skipping it Tracks executed scenarios so that
	 * only they are marked as Pass/Fail later Launches and maximizes the Chrome
	 * browser using WebDriver
	 */
	@Before
	public void setUp(Scenario scenario) {
		Helper.setScenario(scenario);
		String scenarioName = scenario.getName().trim();
		Helper.setScenario(scenario);
		if (!allowedScenarios.contains(scenarioName)) {
			System.out.println("Skipping scenario: " + scenarioName);
			throw new io.cucumber.java.PendingException("Skipped via RunManager.xlsx");
		}

		executedScenarios.add(scenarioName); // track only executed ones

		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
	}

	/*
	 * After every step: If the scenario fails, takes a screenshot using Selenium
	 * Attaches the screenshot to the scenario report (Cucumber HTML report supports
	 * this) *
	 */
	@AfterStep
	public void captureScreenshot(Scenario scenario) {
		if (scenario.isFailed() && driver != null) {
			TakesScreenshot ts = (TakesScreenshot) driver;
			byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
			scenario.attach(screenshot, "image/png", "Failed Step Screenshot");
		}
	}

	/*
	 * After the scenario finishes: If the scenario was actually executed (not
	 * skipped), write "Pass" or "Fail" into RunManager.xlsx Then quits the browser
	 * to clean up *
	 */
	@After
	public void tearDown(Scenario scenario) {
		String scenarioName = scenario.getName().trim();

		// Only write status if scenario was actually executed
		if (executedScenarios.contains(scenarioName)) {
			String status = scenario.isFailed() ? "Fail" : "Pass"; // used conditional clause operator fpr assertion
			Helper.writeScenarioStatus(filePath, scenarioName, status);
		}

		if (driver != null) {
			driver.quit();
		}
	}

	/*
	 * Reads the first sheet of RunManager.xlsx Parses: Column B (index 1): Scenario
	 * Name && Column C (index 2): Run Flag ("Yes"/"No") Returns a list of scenario
	 * names to be run
	 * 
	 */
	private static List<String> getScenariosToRun(String filePath) {
		List<String> scenariosToRun = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;

				String name = Helper.getCellValue(row.getCell(1)).trim(); // Scenario Name in Col B
				String run = Helper.getCellValue(row.getCell(2)).trim(); // Run Flag in Col C

				if ("Yes".equalsIgnoreCase(run)) {
					scenariosToRun.add(name);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return scenariosToRun;
	}
}
