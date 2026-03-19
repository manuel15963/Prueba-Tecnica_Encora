package pe.com.interbank.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.interbank.application.port.input.TransferServicePort;
import pe.com.interbank.application.port.output.TransferPersistencePort;
import pe.com.interbank.domain.model.Transfer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService  implements TransferServicePort {

    private final TransferPersistencePort transferPersistencePort;

    @Override
    public Mono<Transfer> findById(String id) {
        return transferPersistencePort.findById(id);
    }

    @Override
    public Flux<Transfer> findAll() {
        return transferPersistencePort.findAll();
    }

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return transferPersistencePort.save(transfer);
    }

    @Override
    public Mono<Transfer> update(String id, Transfer transfer) {
        return transferPersistencePort.update(id,transfer);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return transferPersistencePort.deleteById(id);
    }
}
