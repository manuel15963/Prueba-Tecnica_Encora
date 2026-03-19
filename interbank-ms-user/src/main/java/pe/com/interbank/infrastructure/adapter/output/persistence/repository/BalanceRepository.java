package pe.com.interbank.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.BalanceEntity;

@Repository
public interface BalanceRepository extends R2dbcRepository<BalanceEntity, String> {
}
