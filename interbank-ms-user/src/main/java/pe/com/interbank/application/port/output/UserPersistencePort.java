package pe.com.interbank.application.port.output;

import pe.com.interbank.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface UserPersistencePort {
    Mono<User> findById(String document);
    Flux<User> findAll();
    Mono<User> update(String id,User user);
    Mono<User> save(User user);
    Mono<Void> delete(String id);
}