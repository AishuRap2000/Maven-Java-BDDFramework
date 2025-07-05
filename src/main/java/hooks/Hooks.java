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

    public static WebDriver driver;
    private static final String filePath = "./RunManager.xlsx";
    private static final List<String> allowedScenarios = getScenariosToRun(filePath);
    private static final Set<String> executedScenarios = new HashSet<>(); // to track only  that really ran

    @Before
    public void setUp(Scenario scenario) {
        String scenarioName = scenario.getName().trim();

        if (!allowedScenarios.contains(scenarioName)) {
            System.out.println("Skipping scenario: " + scenarioName);
            throw new io.cucumber.java.PendingException("Skipped via RunManager.xlsx");
        }

        executedScenarios.add(scenarioName); // track only executed ones

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterStep
    public void captureScreenshot(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failed Step Screenshot");
        }
    }

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

    private static List<String> getScenariosToRun(String filePath) {
        List<String> scenariosToRun = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String name = Helper.getCellValue(row.getCell(1)).trim(); // Scenario Name in Col B
                String run = Helper.getCellValue(row.getCell(2)).trim();  // Run Flag in Col C

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
