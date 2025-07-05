package cucumberOptions;

import io.cucumber.core.cli.Main;

import java.util.ArrayList;
import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        List<String> cucumberOptions = new ArrayList<>();

        cucumberOptions.add("--glue");
        cucumberOptions.add("stepDefinitions");
        cucumberOptions.add("--glue");
        cucumberOptions.add("hooks");

        cucumberOptions.add("--plugin");
        cucumberOptions.add("pretty");
        cucumberOptions.add("--plugin");
        cucumberOptions.add("html:target/cucumber-reports/CucumberReport.html");
        cucumberOptions.add("--plugin");
        cucumberOptions.add("json:target/cucumber-reports/Cucumber.json");
        cucumberOptions.add("--plugin");
        cucumberOptions.add("com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:");

        cucumberOptions.add("src/main/java/feature");

        Main.main(cucumberOptions.toArray(new String[0])); //programmatically run Cucumber from a main() method instead of using the standard @RunWith(Cucumber.class) JUnit runner
    }
}
