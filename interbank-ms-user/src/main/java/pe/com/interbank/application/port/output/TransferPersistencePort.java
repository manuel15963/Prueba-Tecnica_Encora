package pe.com.interbank.application.port.output;

import pe.com.interbank.domain.model.Transfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferPersistencePort {
    Mono<Transfer> findById(String id);

    Flux<Transfer> findAll();

    Mono<Transfer> save(Transfer transfer);

    Mono<Transfer> update(String id, Transfer transfer);
    Mono<Void> deleteById(String id);
}
