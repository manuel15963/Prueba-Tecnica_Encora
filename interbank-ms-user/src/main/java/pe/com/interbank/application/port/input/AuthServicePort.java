package pe.com.interbank.application.port.input;

import pe.com.interbank.domain.model.Login;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.LoginRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.input.RegisterRequest;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthServicePort {
    Mono<AuthResponse> login(LoginRequest request);
    Mono<AuthResponse> register(RegisterRequest request);
    Mono<String> logout(Login login);
    Mono<String> recoverPassword(Login login);
}
