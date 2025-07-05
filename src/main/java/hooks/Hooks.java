package hooks;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void setUp() {
        String projectPath = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", projectPath + "/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterStep
    public void captureScreenshot(Scenario scenario) {
    	if(scenario.isFailed()) {
    		TakesScreenshot ts = (TakesScreenshot)driver;
    		byte[] src = ts.getScreenshotAs(OutputType.BYTES);
    		scenario.attach(src, "image/png", "screenshot");
    		System.out.println("Test caseis failed and screenshot captured for the failed step");
    	}
    }
    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
