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
import pe.com.interbank.application.port.input.TransferServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.application.service.JwtService;
import pe.com.interbank.domain.model.Transfer;
import pe.com.interbank.infrastructure.adapter.config.ApplicationConfig;
import pe.com.interbank.infrastructure.adapter.config.SecurityConfig;
import pe.com.interbank.infrastructure.adapter.input.rest.mapper.TransferRestMapper;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferStatusEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.enums.TransferTypeEnum;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.TransferRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.TransferResponse;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.TransferRepository;
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
@WebFluxTest(controllers = TransferController.class)
@Import({SecurityConfig.class, ApplicationConfig.class, JwtAuthenticationManager.class, JwtService.class, JwtServerAuthenticationConverter.class, JwtTokenizer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TransferServicePort servicePort;

    @MockitoBean
    private TransferRestMapper restMapper;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private TransferRepository transferRepository;

    @MockitoBean
    private DataMaskingServicePort dataMaskingService;

    @BeforeEach
    void setupMasking() {
        lenient().when(dataMaskingService.maskTransferResponse(any(TransferResponse.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findAll_returnsListOfTransfers() {
        Transfer transfer = Transfer.builder()
                .originNumber("9457420")
                .originAccount("1234567890196")
                .targetNumber("945748")
                .targetAccount("123456789011")
                .amount(new BigDecimal("1500.50"))
                .transferType(TransferTypeEnum.TRANSFER)
                .transferStatus(TransferStatusEnum.PENDING)
                .build();

        TransferResponse transferResponse = new TransferResponse("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("1500.50"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING, LocalDateTime.now(),LocalDateTime.now());

        when(servicePort.findAll()).thenReturn(Flux.just(transfer));
        when(restMapper.toTransferResponse(transfer)).thenReturn(transferResponse);

        webTestClient.get()
                .uri("/transfer/api")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransferResponse.class)
                .contains(transferResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void findById_returnsTransfer() {
        String transferId = "12345";
        Transfer transfer = Transfer.builder()
                .originNumber("9457420")
                .originAccount("1234567890196")
                .targetNumber("945748")
                .targetAccount("123456789011")
                .amount(new BigDecimal("1500.50"))
                .transferType(TransferTypeEnum.TRANSFER)
                .transferStatus(TransferStatusEnum.PENDING)
                .build();
        TransferResponse transferResponse = new TransferResponse("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("1500.50"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING, LocalDateTime.now(),LocalDateTime.now());

        when(servicePort.findById(transferId)).thenReturn(Mono.just(transfer));
        when(restMapper.toTransferResponse(transfer)).thenReturn(transferResponse);

        webTestClient.get()
                .uri("/transfer/api/" + transferId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse.class)
                .isEqualTo(transferResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void register_createsTransfer() {
        TransferRequest transferRequest = new TransferRequest("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("1500.50"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING);
        Transfer transfer = Transfer.builder()
                .originNumber("9457420")
                .originAccount("1234567890196")
                .targetNumber("945748")
                .targetAccount("123456789011")
                .amount(new BigDecimal("1500.50"))
                .transferType(TransferTypeEnum.TRANSFER)
                .transferStatus(TransferStatusEnum.PENDING)
                .build();
        TransferResponse transferResponse = new TransferResponse("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("1500.50"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING, LocalDateTime.now(),LocalDateTime.now());

        when(restMapper.toTransfer(transferRequest)).thenReturn(transfer);
        when(servicePort.save(transfer)).thenReturn(Mono.just(transfer));
        when(restMapper.toTransferResponse(transfer)).thenReturn(transferResponse);

        webTestClient.post()
                .uri("/transfer/api")
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransferResponse.class)
                .isEqualTo(transferResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void update_updatesTransfer() {
        String transferId = "12345";
        TransferRequest transferRequest = new TransferRequest("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("2000.00"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING);
        Transfer transfer = Transfer.builder()
                .originNumber("9457420")
                .originAccount("1234567890196")
                .targetNumber("945748")
                .targetAccount("123456789011")
                .amount(new BigDecimal("2000.00"))
                .transferType(TransferTypeEnum.TRANSFER)
                .transferStatus(TransferStatusEnum.PENDING)
                .build();
        TransferResponse transferResponse = new TransferResponse("9457420", "1234567890196", "945748", "123456789011", new BigDecimal("2000.00"), TransferTypeEnum.TRANSFER, TransferStatusEnum.PENDING, LocalDateTime.now(),LocalDateTime.now());

        when(restMapper.toTransfer(transferRequest)).thenReturn(transfer);
        when(servicePort.update(transferId, transfer)).thenReturn(Mono.just(transfer));
        when(restMapper.toTransferResponse(transfer)).thenReturn(transferResponse);

        webTestClient.put()
                .uri("/transfer/api/" + transferId)
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse.class)
                .isEqualTo(transferResponse);
    }

    @Test
    @WithMockUser(username = "johndoe", roles = {"USER"})
    void deleteById_deletesTransfer() {
        String transferId = "12345";

        when(servicePort.deleteById(transferId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/transfer/api/" + transferId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
