Feature: Basic Information Inputs
Background: 
Given Launch browser and navigate to test automation website
@tag1
  Scenario: Text Fields Input(As List)
    When I enter text inputs for Name "Aishwarya", Email,Phone and Address
@tag2
  Scenario: RadioButton, Checkbox and dropdown selection
  	When I select Gender "female"
  	And I check Days
  	And I select Country dropdown
 @tag3
 	Scenario: Sorting and Date Selection
  	Then I verify the sorting order
  	When I select Date Picker