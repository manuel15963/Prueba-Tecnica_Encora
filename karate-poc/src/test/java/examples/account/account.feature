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

  Scenario: Consultar todos las cuentas por documento
    * def id = 460865541
    Given path '/account/api/all', id
    When method get
    Then status 200

  Scenario: Consultar usuario por ID
    * def id = 945748
    Given path '/account/api', id
    When method get
    Then status 200

  Scenario: registar cuenta
    * def register_account =
    """
    {
            "phoneNumber": "945748",
            "document": "460865541",
            "accountNumber": "123456789011",
            "bankingEntity": "Banco Nacional",
            "deviceSerial": "DEVICE123",
            "dailyLimit": 1000.00,
            "operationLimit": 5000.00,
            "username": "johndoe2",
            "password": "securePassword123",
            "currency": "PEN"
    }
    """

    Given path '/account/api'
    And request register_account
    When method post
    Then status 201

  Scenario: Actualizar cuenta por ID
    * def id = 945748
    * def update_account =
    """
    {
      "bankingEntity": "Banco Nacional",
      "dailyLimit": 2000.00
    }
    """

    Given path '/account/api', id
    And request update_account
    When method put
    Then status 200

  Scenario: Eliminar Cuenta por ID
    * def id = 945749

    Given path '/account/api', id
    When method delete
    Then status 204
