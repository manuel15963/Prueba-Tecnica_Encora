package pe.com.interbank.infrastructure.adapter.output.persistence.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.TransferEntity;

@Repository
public interface TransferRepository extends R2dbcRepository<TransferEntity, String> {
}
