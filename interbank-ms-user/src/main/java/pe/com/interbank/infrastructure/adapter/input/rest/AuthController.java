package pe.com.interbank.infrastructure.adapter.input.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.interbank.application.port.input.AuthServicePort;
import pe.com.interbank.application.port.input.DataMaskingServicePort;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.LoginRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api")
@Slf4j
public class AuthController {

    private final AuthServicePort authService;
    private final DataMaskingServicePort dataMaskingService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .flatMap(dataMaskingService::maskAuthResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody RegisterRequest request) {
        return authService.register(request)
                .flatMap(dataMaskingService::maskAuthResponse)
                .map(login -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(login));
    }
/*
    @PostMapping("/token-refresh")
    public Mono<ResponseEntity<AuthResponse>> tokenRefresh(@RequestBody LoginRequest request) {
        return null;
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@RequestBody LoginRequest loginRequest) {
        return null;
    }

    @PostMapping("/recover-password")
    public Mono<ResponseEntity<String>> recoverPassword(@RequestBody LoginRequest loginRequest) {
        return null;
    }*/
}
