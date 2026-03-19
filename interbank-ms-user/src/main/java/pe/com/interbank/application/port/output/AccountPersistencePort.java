package pe.com.interbank.application.port.output;

import pe.com.interbank.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountPersistencePort {
    Mono<Account> findById(String id);
    Mono<Account> findByUsername(String username);
    Flux<Account> findAllByDocument(String document);
    Mono<Account> update(String id,Account account);
    Mono<Account> save(Account account);
    Mono<Void> deleteById(String id);
}
