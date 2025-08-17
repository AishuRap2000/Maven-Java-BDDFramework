package cucumberOptions;

import io.cucumber.core.cli.Main;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
	public static void main(String[] args) {
		List<String> cucumberOptions = new ArrayList<>();

		// glue to define package where stepdefinitions and hooks reside
		cucumberOptions.add("--glue");
		cucumberOptions.add("stepDefinitions");
		cucumberOptions.add("--glue");
		cucumberOptions.add("hooks");

		cucumberOptions.add("--plugin");
		cucumberOptions.add("pretty"); // consoleoutput
		cucumberOptions.add("--plugin");
		cucumberOptions.add("html:target/cucumber-reports/CucumberReport.html");
		cucumberOptions.add("--plugin");
		cucumberOptions.add("json:target/cucumber-reports/Cucumber.json");
		cucumberOptions.add("--plugin");
		cucumberOptions.add("com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"); // for ententr eporting

		cucumberOptions.add("src/main/java/feature"); // this is where your .feature files must be located).
		/* Additional optional options we can add:
		 threads - for parallel execution of scenarios cucumber.io 
		 tags - to filter scenarios by tag 
		monochrome, --dry-run, etc.
		 */
		Main.main(cucumberOptions.toArray(new String[0])); //It scans the glued packages for step definitions and hooks, loads feature files, executes matching scenarios, and give report generation
		//or
//		Main.run(cucumberOptions.toArray(new String[0]));
		// This kicks off Cucumber’s CLI runner directly from your Java program without using JUnit or TestNG
		// programmatically run Cucumber from a main() method instead of using the
		// standard @RunWith(Cucumber.class) JUnit runner

	}
}
