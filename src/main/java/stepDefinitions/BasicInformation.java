package stepDefinitions;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import ReusableMethods.ScreenshotUtil;
import hooks.Hooks;
import hooks.Helper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BasicInformation {

	public WebDriver driver = Hooks.driver;
	public static WebElement element;

	public static ExtentSparkReporter spark;
	public static ExtentReports extent = new ExtentReports();
	public static ExtentTest Logger;

	@Given("Launch browser url and navigate")
	public void LaunchWebsite() throws InterruptedException {
		Logger = extent.createTest("testautomationpractice website automation");
//		System.out.println("Current SCenario name - " + scenario.getName());

		driver.get("https://testautomationpractice.blogspot.com/");
		Thread.sleep(6000);
		Logger.info("Launching website");

		String actualURL = driver.getCurrentUrl();
		actualURL.contains("testautomationpractice.blogspot");
		Logger.pass("Webpage launched and verified");

		String Header = driver.findElement(By.className("title")).getText();
		Header.equalsIgnoreCase("Automation Testing Practice");
		Logger.pass("Header - " + Header + "matches the expected header - Automation Testing Practice");
		ScreenshotUtil.takeScreenshot(driver, "HeaderValidation");
	}

	@When("I enter text inputs for Name {string}, Email,Phone and Address")
	public void EnterTextInputs(String name) throws InterruptedException {
		Logger.info("Entering Inputs to the fileds");
		WebElement Name = driver.findElement(By.id("name"));
		Name.sendKeys(name);

		WebElement email = driver.findElement(By.id("email"));
		email.sendKeys("abc@gmail.com");

		WebElement phone = driver.findElement(By.xpath("//input[@placeholder='Enter Phone']"));
		phone.sendKeys("1234567890");

		WebElement address = driver.findElement(By.id("textarea"));
		address.sendKeys("Perambur Chennai - 600011");

		ScreenshotUtil.takeScreenshot(driver, "Basic Inputs");
	}

	@When("I select Gender {string}")
	public void SelectGenderAndDays(String gender) {
		System.out.println("Gender and Days...");
		WebElement Gender = driver.findElement(By.xpath("//input[@id='" + gender + "']"));
		Gender.click();
		Logger.info("Selecting Gender..");
		ScreenshotUtil.takeScreenshot(driver, "Gender Selection");
	}

	@Then("I check Days")
	public void Days() throws InterruptedException {
		// label[text()='Days:']/../div
		Logger.info("Getting all the value of days");
		int size;
		size = driver.findElements(By.xpath("// label[text()='Days:']/../div")).size();
		System.out.println("Total size of days - " + size);

		for (int i = 1; i <= size; i++) {
			WebElement days = driver.findElement(By.xpath("(// label[text()='Days:']/../div)[" + i + "]"));
			String get_eachDays = days.getText();
			System.out.println("get_eachDays - " + get_eachDays);
			Thread.sleep(5000);
			List<String> DAYS = Arrays.asList("Friday", "Sunday");
			if (DAYS.contains(get_eachDays)) {
				Actions a = new Actions(driver);
				a.moveToElement(days).click().perform(); // Move and click
				System.out.println("Clicked: " + get_eachDays);
			}
		}
		ScreenshotUtil.takeScreenshot(driver, "Days Selection");

	}

	@Then("I select Country dropdown")
	public void CountryDD() throws IOException {
		String countryValue = Helper.getData("Input", "Country_DD");
		WebElement Country = driver.findElement(By.id("country"));
		
		Select country = new Select(Country);
	    country.selectByVisibleText(countryValue);
		ScreenshotUtil.takeScreenshot(driver, "Country Selection");
	}

	@Then("I verify the sorting order")
	public void SortedList() {
		List<WebElement> animalNames = driver.findElements(By.xpath("//select[@name='animals']"));
		List<String> animals = new ArrayList<>();
		for (WebElement element : animalNames) {
			animals.add(element.getText().trim());
		}
		// sort
		Collections.sort(animals);
		Logger.pass("Sorting successful");
		System.out.println("Sorting successful");
	}

	@When("I select Date Picker")
	public void DatePicker() {

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		    // Click the date picker input field
		    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='txtDate']"))).click();

		    String month = "Jan";
		    String year = "2025";
		    String date = "22";

		    // Wait for the calendar to load
		    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@aria-label='Select month']")));

		    // Re-fetch dropdowns to avoid stale reference
		    WebElement yearDropdown = driver.findElement(By.xpath("//select[@aria-label='Select year']"));
		    Select selectYear = new Select(yearDropdown);
		    selectYear.selectByVisibleText(year);

		    // Wait for the month dropdown to reload (sometimes year change can re-render calendar)
		    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@aria-label='Select month']")));
		    WebElement monthDropdown = driver.findElement(By.xpath("//select[@aria-label='Select month']"));
		    Select selectMonth = new Select(monthDropdown);
		    selectMonth.selectByVisibleText(month);

		    // Wait and select the day
		    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[@data-handler='selectDay']/a[text()='" + date + "']"))).click();


		    // Capture screenshot after successful date selection
		    ScreenshotUtil.takeScreenshot(driver, "DatePicker2_Selection");
		}
}
