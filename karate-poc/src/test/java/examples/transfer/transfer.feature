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

  Scenario: Registrar una nueva transferencia
    * def new_transfer =
    """
    {
      "originNumber": "9457420",
      "originAccount": "1234567890196",
      "targetNumber": "945748",
      "targetAccount": "123456789011",
      "amount": 1500.50,
      "transferType": "TRANSFER",
      "transferStatus": "PENDING"
    }
    """
    Given path '/transfer/api'
    And request new_transfer
    When method post
    Then status 201
    And match response contains { originNumber: "9457420", amount: 1500.50 }

  Scenario: Obtener todos las transferencias
    Given path '/transfer/api'
    When method get
    Then status 200

  Scenario: Obtener transferencia por ID
    * def originNumber = '9457420'
    Given path '/transfer/api', originNumber
    When method get
    Then status 200
    And match response contains { originNumber: '#(originNumber)' }

  Scenario: Actualizar una nueva transferencia xistente
    * def originNumber = '9457420'
    * def update_transfer =
    """
    {
      "amount": 2000.00
    }
    """
    Given path '/transfer/api', originNumber
    And request update_transfer
    When method put
    Then status 200
    And match response.amount == 2000.00

  Scenario: Eliminar transferencia por ID
    * def originNumber = '9457420'

    Given path '/transfer/api', originNumber
    When method delete
    Then status 204
