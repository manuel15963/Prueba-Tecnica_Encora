package pe.com.interbank.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.com.interbank.application.port.input.AccountServicePort;
import pe.com.interbank.application.port.output.AccountPersistencePort;
import pe.com.interbank.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements AccountServicePort {

    private final AccountPersistencePort accountPersistencePort;
    private final BCryptPasswordEncoder passwordEncoder;


    @Override
    public Mono<Account> findById(String id) {
        return this.accountPersistencePort.findById(id);
    }

    @Override
    public Mono<Account> findByUsername(String username) {
        return this.accountPersistencePort.findByUsername(username);
    }

    @Override
    public Flux<Account> findAllByDocument(String document) {
        return this.accountPersistencePort.findAllByDocument(document);
    }

    @Override
    public Mono<Account> save(Account account) {
        return encodePassword(account)
                .flatMap(accountPersistencePort::save);
    }

    @Override
    public Mono<Account> update(String id, Account account) {
        return encodePassword(account)
                .flatMap(encoded -> accountPersistencePort.update(id, encoded));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return accountPersistencePort.deleteById(id);
    }

    private Mono<Account> encodePassword(Account account) {
        return Mono.justOrEmpty(account.getPassword())
                .map(passwordEncoder::encode)
                .doOnNext(account::setPassword)
                .thenReturn(account);
    }
}
