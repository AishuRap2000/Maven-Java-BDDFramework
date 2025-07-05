Feature: Basic Information Inputs

Background:
  Given Launch browser and navigate to test automation website

Scenario: Text Fields Input(As List)
  When I enter text inputs for Name "Aishwarya", Email,Phone and Address

Scenario: RadioButton, Checkbox and dropdown selection
  When I select Gender "female"
  And I check Days
  And I select Country dropdown

Scenario: Sorting and Date Selection
  Then I verify the sorting order
  When I select Date Picker
