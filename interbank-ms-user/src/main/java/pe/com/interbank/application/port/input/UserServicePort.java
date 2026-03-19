package pe.com.interbank.application.port.input;

import pe.com.interbank.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserServicePort {
    Mono<User> findById(String id);
    Flux<User> findAll();
    Mono<User> save(User user);
    Mono<User> update(String id, User user);
    Mono<Void> deleteById(String id);

}