Feature: Customer Management
  As a bank administrator
  I want to manage customer accounts
  So that I can ensure proper banking operations

  Scenario: Create a new customer successfully
    Given I am on the customer creation page
    When I enter "John" as first name and "Doe" as last name and "john.doe@example.com" as email
    And I click the "Create Customer" button
    Then a new customer with email "john.doe@example.com" should be created

  Scenario: Create customer with invalid email
    Given I am on the customer creation page
    When I enter "Bob" as first name and "Jones" as last name and "invalid-email" as email
    And I click the "Create Customer" button
    Then the system should reject the request with a validation error

  Scenario: Retrieve an existing customer
    Given a customer with email "alice@example.com" exists in the system
    When I request the customer details by ID 1
    Then I should receive the customer information with first name "Alice"

  Scenario: List all customers
    Given multiple customers exist in the system
    When I request the list of all customers
    Then I should receive a list containing at least 2 customers
