package pe.com.interbank.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.com.interbank.application.port.output.AccountPersistencePort;
import pe.com.interbank.domain.model.Account;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.AccountEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.mapper.AccountPersistenceMapper;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.AccountRepository;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

import static pe.com.interbank.utils.Constants.ACCOUNT;
import static pe.com.interbank.utils.Constants.USER;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountRestAdapter implements AccountPersistencePort {

    private final AccountRepository accountRepository;
    private final AccountPersistenceMapper mapper;

    @Override
    public Flux<Account> findAllByDocument(String document) {
        return accountRepository.findAllByDocument(document)
                .map(mapper::toAccount);
    }

    @Override
    public Mono<Account> findById(String id) {
        return accountRepository.findById(id)
                .map(mapper::toAccount);
    }

    @Override
    public Mono<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .map(mapper::toAccount);
    }


    @Override
    public Mono<Account> save(Account account) {
        return Mono.defer(() -> {
            AccountEntity entity = createAccount(account);
            return accountRepository.save(entity)
                    .map(mapper::toAccount)
                    .doOnError(error -> log.error(Constants.ERROR_SAVING, ACCOUNT, error.getMessage(), error));
        });
    }
    @Override
    public Mono<Account> update(String id, Account account) {
        return Mono.defer(() -> accountRepository.findById(id)
                .flatMap(existingAccount -> updateAndSave(existingAccount, account))
                .doOnError(error -> log.error(Constants.ERROR_SAVING, ACCOUNT, error.getMessage(), error)));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return accountRepository.deleteById(id)
                .doOnError(error -> log.error(Constants.ERROR_DELETED, Constants.ACCOUNT, error.getMessage(), error));
    }

    private AccountEntity createAccount(Account account) {
        AccountEntity entity = mapper.toAccountEntity(account);
        entity.setCreatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        entity.setNewEntry(true);
        return entity;
    }

    private Mono<Account> updateAndSave(AccountEntity existingAccount, Account account) {
        updateAccount(existingAccount, account);
        existingAccount.setUpdatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        existingAccount.setNewEntry(false);
        return accountRepository.save(existingAccount).map(mapper::toAccount);
    }

    private void updateAccount(AccountEntity existingAccount, Account account) {
        Optional.ofNullable(account.getAccountNumber()).ifPresent(existingAccount::setAccountNumber);
        Optional.ofNullable(account.getBankingEntity()).ifPresent(existingAccount::setBankingEntity);
        Optional.ofNullable(account.getDeviceSerial()).ifPresent(existingAccount::setDeviceSerial);
        Optional.ofNullable(account.getDailyLimit()).ifPresent(existingAccount::setDailyLimit);
        Optional.ofNullable(account.getOperationLimit()).ifPresent(existingAccount::setOperationLimit);
        Optional.ofNullable(account.getPassword()).ifPresent(existingAccount::setPassword);
        Optional.ofNullable(account.getCurrency()).ifPresent(existingAccount::setCurrency);
    }

}
