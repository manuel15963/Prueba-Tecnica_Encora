package pe.com.interbank.infrastructure.adapter.input.rest;

import io.jsonwebtoken.impl.JwtTokenizer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeEach;
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
import pe.com.interbank.application.port.input.BalanceServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.service.JwtService;
import pe.com.interbank.domain.model.Balance;
import pe.com.interbank.infrastructure.adapter.config.ApplicationConfig;
import pe.com.interbank.infrastructure.adapter.config.SecurityConfig;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.BalanceRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.BalanceRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.BalanceResponse;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.BalanceRepository;
import pe.com.interbank.infrastructure.adapter.security.JwtAuthenticationManager;
import pe.com.interbank.infrastructure.adapter.security.JwtServerAuthenticationConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;
import static org.mockito.ArgumentMatchers.any;

@AutoConfigureWebTestClient(timeout = "30000")
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = BalanceController.class)
@Import({SecurityConfig.class, ApplicationConfig.class, JwtAuthenticationManager.class, JwtService.class, JwtServerAuthenticationConverter.class, JwtTokenizer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BalanceServicePort servicePort;

    @MockitoBean
    private BalanceRestMapper restMapper;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private BalanceRepository balanceRepository;

    @MockitoBean
    private DataMaskingServicePort dataMaskingService;

    @BeforeEach
    void setupMasking() {
        lenient().when(dataMaskingService.maskBalanceResponse(any(BalanceResponse.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findAll_returnsListOfBalances() {
        Balance balance = Balance.builder()
                .phoneNumber("9457420")
                .originAccount("1234567890196")
                .balanceAmount(new BigDecimal("1500.50"))
                .lastUpdate(LocalDateTime.now())
                .build();

        BalanceResponse balanceResponse = new BalanceResponse("9457420", "1234567890196", new BigDecimal("1500.50"), LocalDateTime.now());

        when(servicePort.findAll()).thenReturn(Flux.just(balance));
        when(restMapper.toBalanceResponse(balance)).thenReturn(balanceResponse);

        webTestClient.get()
                .uri("/balance/api")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BalanceResponse.class)
                .contains(balanceResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findById_returnsBalance() {
        String balanceId = "9457420";
        Balance balance = Balance.builder()
                .phoneNumber("9457420")
                .originAccount("1234567890196")
                .balanceAmount(new BigDecimal("1500.50"))
                .lastUpdate(LocalDateTime.now())
                .build();
        BalanceResponse balanceResponse = new BalanceResponse(balanceId, "1234567890196", new BigDecimal("1500.50"), LocalDateTime.now());

        when(servicePort.findById(balanceId)).thenReturn(Mono.just(balance));
        when(restMapper.toBalanceResponse(balance)).thenReturn(balanceResponse);

        webTestClient.get()
                .uri("/balance/api/" + balanceId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceResponse.class)
                .isEqualTo(balanceResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void register_createsBalance() {
        BalanceRequest balanceRequest = new BalanceRequest("9457420", "1234567890196", new BigDecimal("1500.50"));
        Balance balance = Balance.builder()
                .phoneNumber("9457420")
                .originAccount("1234567890196")
                .balanceAmount(new BigDecimal("1500.50"))
                .lastUpdate(LocalDateTime.now())
                .build();
        BalanceResponse balanceResponse = new BalanceResponse("9457420", "1234567890196", new BigDecimal("1500.50"), LocalDateTime.now());

        when(restMapper.toBalance(balanceRequest)).thenReturn(balance);
        when(servicePort.save(balance)).thenReturn(Mono.just(balance));
        when(restMapper.toBalanceResponse(balance)).thenReturn(balanceResponse);

        webTestClient.post()
                .uri("/balance/api")
                .bodyValue(balanceRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BalanceResponse.class)
                .isEqualTo(balanceResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void update_updatesBalance() {
        String balanceId = "9457420";
        BalanceRequest balanceRequest = new BalanceRequest(balanceId, "1234567890196", new BigDecimal("2000.00"));
        Balance balance = Balance.builder()
                .phoneNumber("9457420")
                .originAccount("1234567890196")
                .balanceAmount(new BigDecimal("1500.50"))
                .lastUpdate(LocalDateTime.now())
                .build();
        BalanceResponse balanceResponse = new BalanceResponse(balanceId, "1234567890196", new BigDecimal("2000.00"), LocalDateTime.now());

        when(restMapper.toBalance(balanceRequest)).thenReturn(balance);
        when(servicePort.update(balanceId, balance)).thenReturn(Mono.just(balance));
        when(restMapper.toBalanceResponse(balance)).thenReturn(balanceResponse);

        webTestClient.put()
                .uri("/balance/api/" + balanceId)
                .bodyValue(balanceRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceResponse.class)
                .isEqualTo(balanceResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void deleteById_deletesBalance() {
        String balanceId = "9457420";

        when(servicePort.deleteById(balanceId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/balance/api/" + balanceId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
