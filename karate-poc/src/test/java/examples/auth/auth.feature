Feature: sample karate test script
  for help, see: https://github.com/karatelabs/karate/wiki/IDE-Support

  Background:
    * url 'http://localhost:9091'

  Scenario: post auth create user
    * def auth_register =
      """
      {
        "document": "460865544",
        "typeDocument": "DNI",
        "firstname": "John",
        "lastname": "Doe",
        "address": "123 Main Street, Lima",
        "email": "john.doe@example.com",
        "phoneNumber": "945749",
        "username": "johndoe",
        "password": "securePassword123",
        "accountNumber": "1234567890196",
        "bankingEntity": "Banco Nacional"
        "deviceSerial": "DEVICE123",
        "currency": "PEN"
      }
      """

    Given path '/auth/api/register'
    And request auth_register
    When method post
    Then status 201

  Scenario: post auth user
    * def auth_login =
      """
      {
        "username": "johndoe",
        "password": "securePassword123"
      }
      """

    Given path '/auth/api/login'
    And request auth_login
    When method post
    Then status 200

  Scenario: post auth create Duplicate user
    * def auth_register =
      """
      {
        "firstname": "John",
        "lastname": "Doe",
        "address": "123 Main Street, Lima",
        "email": "john.doe@example.com",
        "typeDocument": "DNI",
        "document": "460865541",
        "phoneNumber": "945749",
        "username": "johndoe1",
        "password": "securePassword1234"
      }
      """

    Given path '/auth/api/register'
    And request auth_register
    When method post
    Then status 500

    And match response.code == 'ERR_GEN_001'
