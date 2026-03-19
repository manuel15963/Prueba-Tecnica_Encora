package pe.com.interbank.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.interbank.application.port.input.BalanceServicePort;
import pe.com.interbank.application.port.output.BalancePersistencePort;
import pe.com.interbank.domain.model.Balance;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService implements BalanceServicePort {

    private final BalancePersistencePort balancePersistencePort;

    @Override
    public Mono<Balance> findById(String id) {
        return balancePersistencePort.findById(id);
    }

    @Override
    public Flux<Balance> findAll() {
        return balancePersistencePort.findAll();
    }


    @Override
    public Mono<Balance> save(Balance balance) {
        return balancePersistencePort.save(balance);
    }

    @Override
    public Mono<Balance> update(String id, Balance balance) {
        return balancePersistencePort.update(id,balance);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return balancePersistencePort.deleteById(id);
    }

}
