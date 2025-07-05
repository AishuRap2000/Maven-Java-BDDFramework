# Maven-Java-BDDFramework


 Java+Maven project set up for BDD testing with Cucumber, browser automation via Selenium, and data-driven testing using Excel (Apache POI). It likely uses TestNG as the test runne r

 This repository implements a comprehensive Excel-driven BDD framework:
Maven for project management,Cucumber for readable test specification,Apache POI for external test data,Selenium for automating browser interactions,TestNG/JUnit and Cucumber runner to orchestrate and report results

pom.xml – Dependencies & Plugins:
-The key libraries you'll see include:
-Selenium Java for browser automation
-Cucumber Java and TestNG integration for BDD execution
-Apache POI (poi‑ooxml) for Excel read/write
-TestNG (often with scope=test)
- u may also find plugins for test reports, such as Cucumber HTML or Extent.

Gherkin Feature Files:
 syntax tells Cucumber what to test in plain English and references rows in the Excel file for input

Step Definitions: The .java classes in stepDefinitions/ implement these steps

Excel Utility (ExcelUtil.java): Open .xlsx files via FileInputStream , Navigate to specific sheets and rows,Extract cell values,  include write-back methods for results (like pass/fail statuses)

Runner Class : This integrates Cucumber with TestNG or JUnit to execute the feature files

Flow of Execution:
- Maven triggers the test suite (mvn test).
- TestRunner tells Cucumber what to run and where to find steps.
-Cucumber reads .feature files and starts each scenario.
-For each step: The matching Java method is called.If the step uses Excel data, ExcelUtil fetches it.Selenium performs UI actions on your web application.
-Cucumber/TestNG generates reports (HTML, Console, maybe Extent HTML).

Run & Validate: