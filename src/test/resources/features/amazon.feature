Feature: Amazon Website Testing
  As a user
  I want to test basic functionality of Amazon website
  So that I can ensure it works correctly

  Background:
    Given I am on the Amazon homepage

  Scenario: Search for a product
    When I search for "laptop"
    Then I should see search results
    And the page title should contain "laptop"

