package pe.com.interbank.application.port.input;

import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AccountResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.AuthResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.BalanceResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.TransferResponse;
import pe.com.interbank.infrastructure.adapter.input.rest.model.output.UserResponse;
import reactor.core.publisher.Mono;

public interface DataMaskingServicePort {

    Mono<UserResponse> maskUserResponse(UserResponse response);

    Mono<AccountResponse> maskAccountResponse(AccountResponse response);

    Mono<BalanceResponse> maskBalanceResponse(BalanceResponse response);

    Mono<TransferResponse> maskTransferResponse(TransferResponse response);

    Mono<AuthResponse> maskAuthResponse(AuthResponse response);
}

