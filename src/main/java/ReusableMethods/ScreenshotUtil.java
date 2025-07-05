package ReusableMethods;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

public class ScreenshotUtil {

    private static final String BASE_FOLDER = "Execution_Evidence";
    private static final String RUN_TIMESTAMP = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final File RUN_FOLDER = new File(BASE_FOLDER, RUN_TIMESTAMP);

    static {
        // Create base and run folders when class is loaded
        if (!RUN_FOLDER.exists()) {
            boolean created = RUN_FOLDER.mkdirs();
            if (!created) {
                System.err.println("Failed to create run folder: " + RUN_FOLDER.getAbsolutePath());
            }
        }
    }

    public static void takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(RUN_FOLDER, screenshotName + "_" + timestamp + ".png");

        try {
            FileHandler.copy(srcFile, destFile);
            System.out.println("Screenshot saved to: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}
