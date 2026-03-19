package pe.com.interbank.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.AccountEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<AccountEntity, String> {
    Mono<AccountEntity> findByUsername (String username);

    Flux<AccountEntity> findAllByDocument(String document);

}
