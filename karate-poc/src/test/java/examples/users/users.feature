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

  Scenario: post create user
    * def user_register =
      """
      {
        "document": "460865549",
        "typeDocument": "DNI",
        "firstname": "John5",
        "lastname": "Doe",
        "address": "123 Main Street, Lima",
        "email": "john.doe@example.com",
        "phoneNumber": "9457450"
      }
      """

    Given path '/users/api'
    And request user_register
    When method post
    Then status 201

  Scenario: Consultar todos los usuarios
    Given path '/users/api'
    When method get
    Then status 200

  Scenario: Consultar usuario por ID
    * def id = 460865549
    Given path '/users/api', id
    When method get
    Then status 200

  Scenario: Actualizar usuario por ID
    * def id = 460865549
    * def update_user =
    """
    {
      "firstname": "John1",
      "address": "124 Main Street, Lima"
    }
    """

    Given path '/users/api', id
    And request update_user
    When method put
    Then status 200

  Scenario: Eliminar usuario por ID
    * def id = 460865549

    Given path '/users/api', id
    When method delete
    Then status 204
