package pe.com.interbank.infrastructure.adapter.input.rest;

import io.jsonwebtoken.impl.JwtTokenizer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.interbank.application.port.input.AuthServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.service.JwtService;
import pe.com.interbank.domain.exception.DuplicateUserException;
import pe.com.interbank.domain.exception.UserNotFoundException;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.config.ApplicationConfig;
import pe.com.interbank.infrastructure.adapter.config.SecurityConfig;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.RoleEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.LoginRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.UserRepository;
import pe.com.interbank.infrastructure.adapter.security.JwtAuthenticationManager;
import pe.com.interbank.infrastructure.adapter.security.JwtServerAuthenticationConverter;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@AutoConfigureWebTestClient(timeout = "30000")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AuthController.class)
@Import({SecurityConfig.class, ApplicationConfig.class, JwtAuthenticationManager.class, JwtService.class, JwtServerAuthenticationConverter.class, JwtTokenizer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthServicePort authService;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private DataMaskingServicePort dataMaskingService;

    private String jsonRequest;

    @BeforeEach
    void setupMasking() {
        lenient().when(dataMaskingService.maskAuthResponse(any(AuthResponse.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    @Order(1)
    void loginReturnsOkWhenCredentialsAreValid() {
        jsonRequest = "{\"username\":\"johndoe\",\"password\":\"securePassword123\"}";
        User user = User.builder()
                .document("46086503")
                .typeDocument(TypeDocumentEnum.DNI)
                .firstname("John")
                .lastname("Doe")
                .address("123 Main Street, Lima")
                .email("john.doe@example.com")
                .phoneNumber("945749")
                .enabled(true)
                .createdDate(LocalDateTime.parse("2025-01-31T10:21:18"))
                .updatedDate(LocalDateTime.parse("2025-02-04T03:14:37"))
                .build();

        Account account = Account.builder()
                .phoneNumber("945749")
                .document("46086503")
                .accountNumber("123456789012")
                .bankingEntity("Banco Nacional")
                .deviceSerial("DEVICE123")
                .dailyLimit(BigDecimal.valueOf(2000.00))
                .operationLimit(BigDecimal.valueOf(10000.00))
                .username("johndoe")
                .password("$2a$10$Q54YOM3J4e2LLDZsfNmAqu1L/yaHgdsl5So.QA8LhoEKLNQEgLOqS")
                .role(RoleEnum.USER)
                .createdDate(LocalDateTime.parse("2025-01-31T10:21:18"))
                .updatedDate(LocalDateTime.parse("2025-02-04T03:14:37"))
                .build();

        AuthResponse response = new AuthResponse("eyJhbGciOiJIUzI1NiJ9...", user, account);

        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/auth/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(AuthResponse.class)
                .isEqualTo(response);
    }

    @Test
    @Order(2)
    void loginReturnsErrorWhenCredentialsAreInvalid() {
        jsonRequest = "{\"username\":\"johndoe\",\"password\":\"securePassword123\"}";
        when(authService.login(any(LoginRequest.class))).thenReturn(Mono.error(new UserNotFoundException("Invalid credentials.")));

        webTestClient.post()
                .uri("/auth/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(3)
    void registerReturnsCreatedWhenUserIsNew() {
        jsonRequest = """
                {
                  "document": "46086503",
                  "typeDocument": "DNI",
                  "phoneNumber": "945749",
                  "document": "46086503",
                  "accountNumber": "123456789012",
                  "bankingEntity": "Banco Nacional",
                  "deviceSerial": "DEVICE123",
                  "dailyLimit": 2000.00,
                  "operationLimit": 10000.00,
                  "firstname": "John",
                  "lastname": "Doe",
                  "address": "123 Main Street, Lima",
                  "email": "john.doe@example.com",
                  "role": "USER",
                  "username": "johndoe",
                  "password": "securePassword123"
                  } \s""";
        User user = User.builder()
                .document("46086503")
                .typeDocument(TypeDocumentEnum.DNI)
                .firstname("John")
                .lastname("Doe")
                .address("123 Main Street, Lima")
                .email("john.doe@example.com")
                .phoneNumber("945749")
                .enabled(true)
                .createdDate(LocalDateTime.parse("2025-01-31T10:21:18"))
                .updatedDate(LocalDateTime.parse("2025-02-04T03:14:37"))
                .build();

        Account account = Account.builder()
                .phoneNumber("945749")
                .document("46086503")
                .accountNumber("123456789012")
                .bankingEntity("Banco Nacional")
                .deviceSerial("DEVICE123")
                .dailyLimit(BigDecimal.valueOf(2000.00))
                .operationLimit(BigDecimal.valueOf(10000.00))
                .username("johndoe")
                .password("$2a$10$Q54YOM3J4e2LLDZsfNmAqu1L/yaHgdsl5So.QA8LhoEKLNQEgLOqS")
                .role(RoleEnum.USER)
                .createdDate(LocalDateTime.parse("2025-01-31T10:21:18"))
                .updatedDate(LocalDateTime.parse("2025-02-04T03:14:37"))
                .build();
        AuthResponse response = new AuthResponse("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGFtayIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNzM3OTk0NzExLCJleHAiOjE3Mzc5OTY1MTF9.HJcs1xx2LBDnRHshpUTqUPxs-P92Dje97jgkG2cy0C0", user, account);
        when(authService.register(any(RegisterRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/auth/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AuthResponse.class)
                .isEqualTo(response);
    }

    @Test
    @Order(4)
    void registerReturnsErrorWhenUsernameExists() {
        jsonRequest = """
            {
              "firstname": "John",
              "lastname": "Doe",
              "address": "123 Main Street, Lima",
              "email": "john.doe@example.com",
              "username": "johndoe",
              "password": "securePassword123"
            } \s""";
        when(authService.register(any(RegisterRequest.class))).thenReturn(Mono.error(new DuplicateUserException("Username already exists.")));

        webTestClient.post()
                .uri("/auth/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}