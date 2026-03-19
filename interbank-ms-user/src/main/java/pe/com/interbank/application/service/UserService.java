package pe.com.interbank.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.com.interbank.application.port.input.UserServicePort;
import pe.com.interbank.application.port.output.UserPersistencePort;
import pe.com.interbank.domain.exception.UserNotFoundException;
import pe.com.interbank.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static pe.com.interbank.utils.ErrorCatalog.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserServicePort {

    private final UserPersistencePort userPersistencePort;

    @Override
    public Mono<User> findById(String id) {
        return this.userPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(USER_NOT_FOUND.getTitle())));
    }

    @Override
    public Flux<User> findAll() {
        return this.userPersistencePort.findAll();
    }

    @Override
    public Mono<User> save(User user) {
        return  this.userPersistencePort.save(user);
    }

    @Override
    public Mono<User> update(String id, User user) {
        return userPersistencePort.update(id,user);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return userPersistencePort.delete(id);

    }

}
