package stepDefinitions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

public class ScreenshotUtil {

	public static void takeScreenshot(WebDriver driver, String screenshotName) {
		File evidenceDir = new File("Execution_Evidence");
		if(!evidenceDir.exists()) {
			evidenceDir.mkdir();
		}
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File destFile = new File(evidenceDir + File.separator + screenshotName + "_" + timestamp + ".png");

		try {
			FileHandler.copy(srcFile, destFile);
			System.out.println("Screenshot saved to : " +destFile.getAbsolutePath());

		} catch(IOException e) {
			System.out.println("Failed to save screenshot : " +e.getMessage());
		}
	}
}
