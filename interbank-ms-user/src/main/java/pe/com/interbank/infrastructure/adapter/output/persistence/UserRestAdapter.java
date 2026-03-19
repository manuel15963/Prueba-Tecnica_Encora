package pe.com.interbank.infrastructure.adapter.output.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.com.interbank.application.port.output.UserPersistencePort;
import pe.com.interbank.domain.exception.DuplicateUserException;
import pe.com.interbank.domain.exception.NotFoundException;
import pe.com.interbank.domain.model.User;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.TransferEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.UserEntity;
import pe.com.interbank.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import pe.com.interbank.infrastructure.adapter.output.persistence.repository.UserRepository;
import pe.com.interbank.utils.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

import static pe.com.interbank.utils.Constants.USER;
import static pe.com.interbank.utils.ErrorCatalog.USER_DUPLICATE;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRestAdapter implements UserPersistencePort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .map(mapper::toUser)
                .doOnError(error -> log.error("Error fetching user by id {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll()
                .map(mapper::toUser)
                .doOnError(error -> log.error("Error fetching all users: {}", error.getMessage(), error));
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.defer(() -> {
            UserEntity entity = createUser(user);
            return userRepository.save(entity)
                    .map(mapper::toUser)
                    .doOnError(error -> log.error(Constants.ERROR_SAVING, USER, error.getMessage(), error));
        });
    }

    private UserEntity createUser(User user) {
        UserEntity entity = mapper.toUserEntity(user);
        entity.setCreatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        entity.setNewEntry(true);
        return entity;
    }

    @Override
    public Mono<User> update(String id, User user) {
        return Mono.defer(() -> userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + id)))
                .flatMap(existingUser -> {
                    updateUser(existingUser, user);
                    return userRepository.save(existingUser);
                })
                .map(mapper::toUser)
                .doOnError(error -> log.error(Constants.ERROR_SAVING, Constants.USER, error.getMessage(), error))
        );
    }

    private void  updateUser(UserEntity existingUser, User user) {
        Optional.ofNullable(user.getFirstname()).ifPresent(existingUser::setFirstname);
        Optional.ofNullable(user.getLastname()).ifPresent(existingUser::setLastname);
        Optional.ofNullable(user.getAddress()).ifPresent(existingUser::setAddress);
        Optional.ofNullable(user.getEmail()).ifPresent(existingUser::setEmail);
        existingUser.setUpdatedDate(Constants.convertToLocalTimeZone(LocalDateTime.now()));
        existingUser.setNewEntry(false);
    }

    @Override
    public Mono<Void> delete(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + id))) // Manejo de error
                .flatMap(user -> {
                    user.setEnabled(false);
                    user.setNewEntry(false);
                    return userRepository.save(user);
                })
                .then()
                .doOnError(error -> log.error("Error disabling user with id {}: {}", id, error.getMessage(), error));
    }

}