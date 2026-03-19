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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.interbank.application.port.input.AccountServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.service.JwtService;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.infrastructure.adapter.config.ApplicationConfig;
import pe.com.interbank.infrastructure.adapter.config.SecurityConfig;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.AccountRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.CurrencyEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.RoleEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.AccountRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AccountResponse;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.infrastructure.adapter.security.JwtAuthenticationManager;
import pe.com.interbank.infrastructure.adapter.security.JwtServerAuthenticationConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient(timeout = "30000")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AccountController.class)
@Import({SecurityConfig.class, ApplicationConfig.class, JwtAuthenticationManager.class, JwtService.class, JwtServerAuthenticationConverter.class, JwtTokenizer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountServicePort servicePort;

    @MockitoBean
    private AccountRestMapper restMapper;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private DataMaskingServicePort dataMaskingService;

    @BeforeEach
    void setupMasking() {
        lenient().when(dataMaskingService.maskAccountResponse(any(AccountResponse.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    @Order(1)
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findAllByDocument_returnsListOfAccounts() {
        String documentId = "460865541";
        Account account = new Account();
        AccountResponse accountResponse = new AccountResponse(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                RoleEnum.USER,
                CurrencyEnum.PEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicePort.findAllByDocument(documentId)).thenReturn(Flux.just(account));
        when(restMapper.toAccountResponse(account)).thenReturn(accountResponse);

        webTestClient.get()
                .uri("/account/api/all/" + documentId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponse.class)
                .contains(accountResponse);
    }

    @Test
    @Order(2)
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findById_returnsAccount() {
        String accountId = "945748";
        Account account = new Account();
        AccountResponse accountResponse = new AccountResponse(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                RoleEnum.USER,
                CurrencyEnum.PEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicePort.findById(accountId)).thenReturn(Mono.just(account));
        when(restMapper.toAccountResponse(account)).thenReturn(accountResponse);

        webTestClient.get()
                .uri("/account/api/" + accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponse.class)
                .isEqualTo(accountResponse);
    }

    @Test
    @Order(3)
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void register_createsAccount() {
        AccountRequest accountRequest = new AccountRequest(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                CurrencyEnum.PEN
        );

        Account account = new Account();
        AccountResponse accountResponse = new AccountResponse(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                RoleEnum.USER,
                CurrencyEnum.PEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicePort.save(any(Account.class))).thenReturn(Mono.just(account));
        when(restMapper.toAccount(accountRequest)).thenReturn(account);
        when(restMapper.toAccountResponse(account)).thenReturn(accountResponse);

        webTestClient.post()
                .uri("/account/api")
                .bodyValue(accountRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountResponse.class)
                .isEqualTo(accountResponse);
    }

    @Test
    @Order(4)
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void update_updatesAccount() {
        String accountId = "945748";
        AccountRequest accountRequest = new AccountRequest(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                CurrencyEnum.PEN
        );

        Account account = new Account();
        AccountResponse accountResponse = new AccountResponse(
                "945748",
                "460865541",
                "123456789011",
                "Banco Nacional",
                "DEVICE123",
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(5000.00),
                "johndoe2",
                "securePassword123",
                RoleEnum.USER,
                CurrencyEnum.PEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(servicePort.update(eq(accountId), any(Account.class))).thenReturn(Mono.just(account));
        when(restMapper.toAccount(accountRequest)).thenReturn(account);
        when(restMapper.toAccountResponse(account)).thenReturn(accountResponse);

        webTestClient.put()
                .uri("/account/api/" + accountId)
                .bodyValue(accountRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponse.class)
                .isEqualTo(accountResponse);
    }

    @Test
    @Order(5)
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void deleteById_deletesAccount() {
        String accountId = "945748";

        when(servicePort.deleteById(accountId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/account/api/" + accountId)
                .exchange()
                .expectStatus().isNoContent();
    }
}

