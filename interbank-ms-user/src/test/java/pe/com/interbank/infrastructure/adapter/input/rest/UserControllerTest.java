package pe.com.interbank.infrastructure.adapter.input.rest;

import io.jsonwebtoken.impl.JwtTokenizer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.interbank.application.port.input.UserServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.service.JwtService;
import pe.com.interbank.domain.exception.UserNotFoundException;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.config.ApplicationConfig;
import pe.com.interbank.infrastructure.adapter.config.SecurityConfig;
import pe.com.interbank.infrastructure.adapter.input.rest.filter.CorrelationIdWebFilter;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.UserRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TypeDocumentEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UpdateRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.UserRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.UserResponse;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.UserRepository;
import pe.com.interbank.infrastructure.adapter.security.JwtAuthenticationManager;
import pe.com.interbank.infrastructure.adapter.security.JwtServerAuthenticationConverter;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient(timeout = "30000")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserController.class)
@Import({SecurityConfig.class, ApplicationConfig.class, JwtAuthenticationManager.class, JwtService.class, JwtServerAuthenticationConverter.class, JwtTokenizer.class, CorrelationIdWebFilter.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserServicePort userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private UserRestMapper restMapper;

    @MockitoBean
    private DataMaskingServicePort dataMaskingService;

    @BeforeEach
    void setupMasking() {
        lenient().when(dataMaskingService.maskUserResponse(any(UserResponse.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }


    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(1)
    void findAll_returnsListOfUsers() {
        // Configuración de datos de prueba
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

        UserResponse userResponse = new UserResponse(
                user.getDocument(),
                user.getTypeDocument(),
                user.getFirstname(),
                user.getLastname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getEnabled(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );

        when(userService.findAll()).thenReturn(Flux.just(user));
        when(restMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // Ejecución de la solicitud y verificación
        webTestClient.get()
                .uri("/users/api")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .contains(userResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(2)
    void findById_returnsUser() {

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
        UserResponse userResponse = new UserResponse(
                user.getDocument(),
                user.getTypeDocument(),
                user.getFirstname(),
                user.getLastname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getEnabled(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );

        when(userService.findById("46086503")).thenReturn(Mono.just((user)));
        when(restMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get()
                .uri("/users/api/46086503")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .isEqualTo(userResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(3)
    void update_returnsUpdatedUser() {
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

        User userUpdate = User.builder()
                .document("46086503")
                .typeDocument(TypeDocumentEnum.DNI)
                .firstname("John")
                .lastname("Doe")
                .address("124 Main Street, Lima\"")
                .email("john.doe@example.com")
                .phoneNumber("945749")
                .enabled(true)
                .createdDate(LocalDateTime.parse("2025-01-31T10:21:18"))
                .updatedDate(LocalDateTime.parse("2025-02-04T03:14:37"))
                .build();

        UserResponse userResponse = new UserResponse(
                user.getDocument(),
                user.getTypeDocument(),
                user.getFirstname(),
                user.getLastname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getEnabled(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );
        UpdateRequest updateRequest = new UpdateRequest(null,null,"124 Main Street, Lima",null,null,null, null, null, null);
        when(userService.update(anyString(), any(User.class))).thenReturn(Mono.just(userUpdate));
        when(restMapper.toUser(any(UpdateRequest.class))).thenReturn(userUpdate);
        when(restMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.put()
                .uri("/users/api/46086503")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .isEqualTo(userResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(4)
    void delete_deletesUser() {
        when(userService.deleteById("46086503")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/api/46086503")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(5)
    void findAll_returnsEmptyListWhenNoUsers() {
        when(userService.findAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/users/api")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(0);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(6)
    void findById_returnsNotFoundWhenUserDoesNotExist() {
        when(userService.findById("46086503")).thenReturn(Mono.error(new UserNotFoundException("User Not Found.")));

        webTestClient.get()
                .uri("/users/api/46086503")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(7)
    void update_returnsNotFoundWhenUserDoesNotExist() {
        UpdateRequest updateRequest = new UpdateRequest(null, null, "124 Main Street, Lima", null, null, null, null, null, null);

        // Simulaciones
        when(userService.findById("46086503")).thenReturn(Mono.empty());
        when(userService.update(eq("46086503"), any(User.class)))
                .thenReturn(Mono.error(new UserNotFoundException("User Not Found.")));
        when(restMapper.toUser(any(UpdateRequest.class))).thenReturn(new User());

        // Ejecución de la prueba
        webTestClient.put()
                .uri("/users/api/46086503")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(8)
    void register_returnsCreatedUser() {
        UserRequest request = new UserRequest(
                "46086503",
                TypeDocumentEnum.DNI,
                "John",
                "Doe",
                "123 Main Street, Lima",
                "john.doe@example.com",
                "945749"
        );

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

        UserResponse response = new UserResponse(
                user.getDocument(),
                user.getTypeDocument(),
                user.getFirstname(),
                user.getLastname(),
                user.getAddress(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getEnabled(),
                user.getCreatedDate(),
                user.getUpdatedDate()
        );

        when(restMapper.toUser(any(UserRequest.class))).thenReturn(user);
        when(userService.save(any(User.class))).thenReturn(Mono.just(user));
        when(restMapper.toUserResponse(any(User.class))).thenReturn(response);

        webTestClient.post()
                .uri("/users/api")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .isEqualTo(response);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(9)
    void findById_returnsBadRequestWhenIdIsInvalid() {
        webTestClient.get()
                .uri("/users/api/12")
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    @Order(10)
    void findAll_echoesCorrelationIdHeader() {
        when(userService.findAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/users/api")
                .header(Constants.CORRELATION_ID_HEADER, "corr-test-001")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(Constants.CORRELATION_ID_HEADER, "corr-test-001");
    }

}