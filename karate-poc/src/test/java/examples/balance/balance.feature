Feature: Sample Karate Test Script
# Para más ayuda: https://github.com/karatelabs/karate/wiki/IDE-Support

  Background:
    * url 'http://localhost:9091'
    * def auth_login =
    """
    {
      "username": "johndoe",
      "password": "securePassword123"
    }
    """
  # Generación del Token
    Given path '/auth/api/login'
    And request auth_login
    When method post
    Then status 200
    * def expectedToken = response.token
    * def authHeader = 'Bearer ' + expectedToken
    * print 'Token generado: ', expectedToken

  # Configuración de headers para todos los escenarios
    * configure headers = { Authorization: '#(authHeader)', Accept: 'application/json' }

  Scenario: Registrar un nuevo balance
    * def new_balance =
    """
    {
      "phoneNumber": "9457420",
      "originAccount": "1234567890196",
      "balanceAmount": 1500.50
    }
    """
    Given path '/balance/api'
    And request new_balance
    When method post
    Then status 201
    And match response contains { phoneNumber: "9457420", balanceAmount: 1500.50 }

  Scenario: Obtener todos los balances
    Given path '/balance/api'
    When method get
    Then status 200

  Scenario: Obtener un balance por ID
    * def phoneNumber = '9457420'
    Given path '/balance/api', phoneNumber
    When method get
    Then status 200
    And match response contains { phoneNumber: '#(phoneNumber)' }

  Scenario: Actualizar un balance existente
    * def phoneNumber = '9457420'
    * def update_balance =
    """
    {
      "balanceAmount": 2000.00
    }
    """
    Given path '/balance/api', phoneNumber
    And request update_balance
    When method put
    Then status 200
    And match response.balanceAmount == 2000.00

  Scenario: Eliminar balance por ID
    * def phoneNumber = '9457420'

    Given path '/balance/api', phoneNumber
    When method delete
    Then status 204
