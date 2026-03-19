package pe.com.interbank.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import pe.com.interbank.domain.model.Balance;
import pe.com.interbank.infrastructure.adapter.output.persistence.entity.BalanceEntity;

@Mapper(componentModel = "spring")
public interface BalancePersistenceMapper {

    BalanceEntity toBalanceEntity(Balance balance);
    Balance toBalance(BalanceEntity balance);
}
