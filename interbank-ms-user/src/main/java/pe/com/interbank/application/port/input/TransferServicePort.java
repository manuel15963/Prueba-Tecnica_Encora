package pe.com.interbank.application.port.input;

import pe.com.interbank.domain.model.Transfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferServicePort {

    Flux<Transfer> findAll();
    Mono<Transfer> findById(String id);
    Mono<Transfer> save(Transfer transfer);
    Mono<Transfer> update(String id, Transfer transfer);
    Mono<Void> deleteById(String id);
}
