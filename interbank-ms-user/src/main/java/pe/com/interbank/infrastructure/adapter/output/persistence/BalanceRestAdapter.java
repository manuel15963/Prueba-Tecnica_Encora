package pe.com.interbank.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.com.interbank.application.port.output.BalancePersistencePort;
import pe.com.interbank.domain.exception.NotFoundException;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.domain.model.Balance;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.AccountEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.BalanceEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.mapper.BalancePersistenceMapper;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.BalanceRepository;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

import static pe.com.interbank.utils.Constants.BALANCE;

@Component
@Slf4j
@RequiredArgsConstructor
public class BalanceRestAdapter implements BalancePersistencePort {

    private final BalanceRepository balanceRepository;
    private final BalancePersistenceMapper mapper;

    @Override
    public Mono<Balance> findById(String id) {
        return balanceRepository.findById(id).map(mapper::toBalance);
    }

    @Override
    public Flux<Balance> findAll() {
        return balanceRepository.findAll().map(mapper::toBalance);
    }

    @Override
    public Mono<Balance> save(Balance balance) {
        return Mono.defer(() -> {
            BalanceEntity entity = createBalance(balance);
            return balanceRepository.save(entity)
                    .map(mapper::toBalance)
                    .doOnError(error -> log.error(Constants.ERROR_SAVING, BALANCE, error.getMessage(), error));
        });
    }

    @Override
    public Mono<Balance> update(String id, Balance balance) {
        return Mono.defer(() -> balanceRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Balance not found with id: " + id)))
                .flatMap(existingBalance -> {
                    updateBalance(existingBalance, balance);
                    return balanceRepository.save(existingBalance);
                })
                .map(mapper::toBalance)
                .doOnError(error -> log.error(Constants.ERROR_SAVING, Constants.BALANCE, error.getMessage(), error)));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return balanceRepository.deleteById(id)
                .doOnError(error -> log.error(Constants.ERROR_DELETED, Constants.BALANCE, error.getMessage(), error));
    }

    private BalanceEntity createBalance(Balance balance) {
        BalanceEntity entity = mapper.toBalanceEntity(balance);
        entity.setLastUpdate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        entity.setNewEntry(true);
        return entity;
    }


    private void  updateBalance(BalanceEntity existingBalance, Balance balance) {
        Optional.ofNullable(balance.getOriginAccount()).ifPresent(existingBalance::setOriginAccount);
        Optional.ofNullable(balance.getBalanceAmount()).ifPresent(existingBalance::setBalanceAmount);
        existingBalance.setLastUpdate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        existingBalance.setNewEntry(false);
    }

}
