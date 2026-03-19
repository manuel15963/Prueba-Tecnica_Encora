package pe.com.interbank.application.port.input;

import pe.com.interbank.domain.model.Balance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BalanceServicePort {

    Mono<Balance> findById(String id);
    Flux<Balance> findAll();
    Mono<Balance> save(Balance balance);
    Mono<Balance> update(String id, Balance balance);
    Mono<Void> deleteById(String id);
}
